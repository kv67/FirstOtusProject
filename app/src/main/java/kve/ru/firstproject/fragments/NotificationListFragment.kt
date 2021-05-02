package kve.ru.firstproject.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.MainActivity
import kve.ru.firstproject.R
import kve.ru.firstproject.adapter.NotificationAdapter
import kve.ru.firstproject.db.Notification
import kve.ru.firstproject.model.FilmViewModel
import kve.ru.firstproject.service.FilmNotificationPublisher
import java.util.*

class NotificationListFragment : Fragment() {

    companion object {
        const val TAG = "NotificationListFragment"
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[FilmViewModel::class.java]
    }
    private var recyclerViewNotifications: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        return inflater.inflate(R.layout.fragment_notification_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().title = getString(R.string.notifications_title)
        setHasOptionsMenu(true)

        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications)
        recyclerViewNotifications?.apply {
            adapter = NotificationAdapter { notification -> setNotification(notification) }
            layoutManager = LinearLayoutManager(requireContext())
        }

        initTouchHelper {
            (recyclerViewNotifications?.adapter as NotificationAdapter).getItemByPos(it)
                ?.let { note ->
                    viewModel.deleteNotification(note.id)
                    MainActivity.showSnackBar(
                        requireView(),
                        getString(R.string.notification_removed_msg),
                        getString(R.string.undo_btn_title)
                    ) {
                        viewModel.addNotification(note)
                    }

                }
        }

        viewModel.notifications.observe(viewLifecycleOwner, { notifications ->
            (recyclerViewNotifications?.adapter as NotificationAdapter).setData(notifications)
        })

        viewModel.getNotifications()
    }

    private fun initTouchHelper(listener: ((id: Int) -> Unit)?) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listener?.invoke(viewHolder.adapterPosition)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerViewNotifications)
    }

    private fun setNotification(notification: Notification) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_Material_Light_Dialog,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                notification.let { note ->
                    val date =
                        "${if (d < 10) "0" else ""}$d.${if (m + 1 < 10) "0" else ""}${m + 1}.$y"
                    viewModel.addNotification(Notification(note.id, note.name, date, note.dsc))
                    Calendar.getInstance().let { cl ->
                        cl.set(y, m, d, 10, 0, 0)
                        FilmNotificationPublisher.sendNotification(
                            requireContext(), note.id, note.name, note.dsc, cl.timeInMillis
                        )
                    }
                }
            },
            year, month, day
        )
        cal.add(Calendar.DATE, 1)
        dialog.datePicker.minDate = cal.timeInMillis
        cal.add(Calendar.DATE, 15)
        dialog.datePicker.maxDate = cal.timeInMillis
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }
}
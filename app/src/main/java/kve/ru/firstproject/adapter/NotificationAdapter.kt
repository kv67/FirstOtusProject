package kve.ru.firstproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import kve.ru.firstproject.R
import kve.ru.firstproject.db.Notification

class NotificationAdapter(
    private val notificationListener: ((notification: Notification) -> Unit)
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val dataList = ArrayList<Notification>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NotificationAdapter.NotificationViewHolder,
        position: Int
    ) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getItemByPos(position: Int): Notification? {
        if (dataList.size < position + 1) {
            return null
        }
        return dataList[position]
    }

    fun setData(notifications: List<Notification>) {
        dataList.clear()
        dataList.addAll(notifications)

        notifyDataSetChanged()
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewFilmName = itemView.findViewById<TextView>(R.id.textViewFilmName)
        private val textViewDate = itemView.findViewById<TextView>(R.id.textViewDate)
        private val layoutItemNotification =
            itemView.findViewById<ConstraintLayout>(R.id.layoutItemNotification)

        init {
            textViewFilmName.setOnClickListener {
                notificationListener.invoke(dataList[adapterPosition])
            }
            textViewDate.setOnClickListener {
                notificationListener.invoke(dataList[adapterPosition])
            }
        }

        fun bind(notification: Notification) {
            textViewFilmName.text = notification.name
            textViewDate.text = notification.date

            if (adapterPosition % 2 == 0) {
                layoutItemNotification.background =
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.white, null
                    ).toDrawable()
            } else {
                layoutItemNotification.background =
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.colorPrimaryLight, null
                    ).toDrawable()
            }
        }
    }
}
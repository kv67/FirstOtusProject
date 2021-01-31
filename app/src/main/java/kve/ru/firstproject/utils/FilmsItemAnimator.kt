package kve.ru.firstproject.utils

import android.animation.ValueAnimator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.AnimationUtils.DECELERATE_INTERPOLATOR
import kve.ru.firstproject.MainActivity.Companion.STAR_ANIMATE
import kve.ru.firstproject.adapter.FilmAdapter

class FilmsItemAnimator : DefaultItemAnimator() {

    private fun animateStar(holder: FilmAdapter.FilmViewHolder) {
        val starView = holder.imageViewStar
        starView.animate().apply {
            setUpdateListener { animation ->
                animation?.let {
                    if (animation.repeatCount == 0) {
                        animation.repeatCount = 1
                        animation.repeatMode = ValueAnimator.REVERSE
                    }
                }
            }

            scaleXBy(2.0f).scaleYBy(2.0f).setInterpolator(DECELERATE_INTERPOLATOR).start()
        }
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        if (preInfo is FilmItemHolderInfo) {
            if (preInfo.isFavorite) {
                animateStar(newHolder as FilmAdapter.FilmViewHolder)
            }
            return true
        }
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {

        if (changeFlags == FLAG_CHANGED) {
            for (payload in payloads) {
                if (payload as? String == STAR_ANIMATE) {
                    //Get the info you need from the viewHolder and save it in your custom ItemHolderInfo
                    return FilmItemHolderInfo((viewHolder as FilmAdapter.FilmViewHolder).isFavorite())
                }
            }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ): Boolean {
        return true
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    class FilmItemHolderInfo(val isFavorite: Boolean) : ItemHolderInfo()
}
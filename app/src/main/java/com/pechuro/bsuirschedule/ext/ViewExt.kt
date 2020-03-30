package com.pechuro.bsuirschedule.ext

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.Px
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.*
import com.pechuro.bsuirschedule.R
import com.pechuro.bsuirschedule.common.ClickInterval
import com.pechuro.bsuirschedule.common.OneFirePerIntervalClickListener
import java.util.*

inline fun View.setSafeClickListener(
        interval: ClickInterval = ClickInterval.NORMAL,
        crossinline onClick: (View) -> Unit
) {
    val safeClickListener = OneFirePerIntervalClickListener(interval.milliseconds) {
        onClick(it)
    }
    setOnClickListener(safeClickListener)
}

inline var View.isVisibleOrInvisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

fun View.setVisibleWithAlpha(isVisible: Boolean): Animator {
    val previousAnimator = getTag(R.id.tagCurrentAnimator) as? Animator
    previousAnimator?.cancel()
    val animator = if (isVisible) {
        animateAlpha(alpha, 1F).apply {
            doOnStart { visibility = View.VISIBLE }
        }
    } else {
        animateAlpha(alpha, 0F).apply {
            doOnEnd { visibility = View.GONE }
        }
    }
    animator.start()
    return animator
}


fun View.setVisibleOrInvisibleWithAlpha(isVisible: Boolean): Animator {
    val previousAnimator = getTag(R.id.tagCurrentAnimator) as? Animator
    previousAnimator?.cancel()
    val animator = if (isVisible) {
        animateAlpha(alpha, 1F).apply {
            doOnStart { visibility = View.VISIBLE }
        }
    } else {
        animateAlpha(alpha, 0F).apply {
            doOnEnd { visibility = View.INVISIBLE }
        }
    }
    animator.start()
    return animator
}

fun View.setHeight(@Px height: Int) = apply {
    layoutParams.height = height
    requestLayout()
}

fun View.updateMargin(
        @Px left: Int = marginLeft,
        @Px top: Int = marginTop,
        @Px right: Int = marginRight,
        @Px bottom: Int = marginBottom
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(left, top, right, bottom)
        }
    }
}

fun DatePicker.getDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    return calendar.time
}
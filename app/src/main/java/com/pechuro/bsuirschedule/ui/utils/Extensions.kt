package com.pechuro.bsuirschedule.ui.utils

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.pechuro.bsuirschedule.data.entity.Schedule
import com.pechuro.bsuirschedule.ui.data.ScheduleInformation
import java.util.*

fun Schedule.getInfo() = ScheduleInformation(id, name, type)

inline fun FragmentManager.transaction(
        now: Boolean = false,
        allowStateLoss: Boolean = false,
        body: FragmentTransaction.() -> Unit
) {
    val transaction = beginTransaction().apply {
        setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_in)
    }
    transaction.body()
    if (now) {
        if (allowStateLoss) {
            transaction.commitNowAllowingStateLoss()
        } else {
            transaction.commitNow()
        }
    } else {
        if (allowStateLoss) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }
}

fun Calendar.addDays(days: Int): Long {
    add(Calendar.DATE, days)
    return timeInMillis
}
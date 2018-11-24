package com.pechuro.bsuirschedule.ui.fragment.adddialog

import com.pechuro.bsuirschedule.ui.utils.BaseEvent

sealed class AddDialogEvent : BaseEvent() {
    object OnSuccess : AddDialogEvent()
    object OnScheduleAdded : AddDialogEvent()
    class OnLoading(val isEnabled: Boolean) : AddDialogEvent()
}

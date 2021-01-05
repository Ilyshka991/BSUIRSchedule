package com.pechuro.bsuirschedule.feature.addschedule

import androidx.lifecycle.MutableLiveData
import com.pechuro.bsuirschedule.common.AppAnalytics
import com.pechuro.bsuirschedule.common.AppAnalyticsEvent
import com.pechuro.bsuirschedule.common.base.BaseViewModel
import com.pechuro.bsuirschedule.domain.common.fold
import com.pechuro.bsuirschedule.domain.entity.*
import com.pechuro.bsuirschedule.domain.interactor.LoadEmployeeSchedule
import com.pechuro.bsuirschedule.domain.interactor.LoadGroupSchedule
import com.pechuro.bsuirschedule.ext.addIfEmpty
import javax.inject.Inject

class AddScheduleViewModel @Inject constructor(
        private val loadEmployeeSchedule: LoadEmployeeSchedule,
        private val loadGroupSchedule: LoadGroupSchedule
) : BaseViewModel() {

    val state = MutableLiveData<State>(State.Idle)

    fun loadSchedule(group: Group, types: List<ScheduleType>) {
        val resultTypes = correlateScheduleTypes(group, types)
        if (resultTypes.isEmpty()) return
        state.value = State.Loading
        launchCoroutine {
            loadGroupSchedule.execute(LoadGroupSchedule.Params(group, resultTypes)).fold(
                    onSuccess = {
                        it.firstOrNull()?.let { schedule ->
                            AppAnalytics.report(AppAnalyticsEvent.AddSchedule.ScheduleLoaded(
                                    schedule = schedule,
                                    types = resultTypes
                            ))
                        }
                        state.value = State.Complete(it)
                    },
                    onFailure = {
                        state.value = State.Error(it)
                    }
            )
        }
    }

    fun loadSchedule(employee: Employee, types: List<ScheduleType>) {
        if (types.isEmpty()) return
        state.value = State.Loading
        launchCoroutine {
            loadEmployeeSchedule.execute(LoadEmployeeSchedule.Params(employee, types)).fold(
                    onSuccess = {
                        it.firstOrNull()?.let { schedule ->
                            AppAnalytics.report(AppAnalyticsEvent.AddSchedule.ScheduleLoaded(
                                    schedule = schedule,
                                    types = types
                            ))
                        }
                        state.value = State.Complete(it)
                    },
                    onFailure = {
                        state.value = State.Error(it)
                    }
            )
        }
    }

    fun cancel() {
        state.value = State.Idle
    }

    private fun correlateScheduleTypes(group: Group, typesToDownload: List<ScheduleType>): List<ScheduleType> {
        val availableTypes = group.availableScheduleTypes
        return typesToDownload
                .intersect(availableTypes)
                .addIfEmpty {
                    availableTypes.firstOrNull()
                }
                .filterNotNull()
                .toList()
    }

    sealed class State {
        object Idle : State()
        object Loading : State()
        data class Error(val exception: Throwable) : State()
        data class Complete(val schedules: List<Schedule>) : State()
    }
}
package com.pechuro.bsuirschedule.domain.interactor

import com.pechuro.bsuirschedule.domain.common.BaseInteractor
import com.pechuro.bsuirschedule.domain.entity.Schedule
import com.pechuro.bsuirschedule.domain.repository.IScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAvailableForUpdateSchedules @Inject constructor(
        private val scheduleRepository: IScheduleRepository
) : BaseInteractor<Flow<List<Schedule>>, GetAvailableForUpdateSchedules.Params>() {

    override suspend fun run(params: Params): Flow<List<Schedule>> {
        return scheduleRepository.getAllSchedules()
                .mapLatest { allSchedules ->
                    allSchedules
                            .filter { schedule ->
                                when (schedule) {
                                    is Schedule.GroupClasses -> !schedule.notRemindForUpdates || params.includeAll
                                    is Schedule.GroupExams -> !schedule.notRemindForUpdates || params.includeAll
                                    else -> false
                                }
                            }
                            .map { schedule ->
                                withContext(Dispatchers.Default) {
                                    async {
                                        schedule to runCatching {
                                            scheduleRepository.isUpdateAvailable(schedule)
                                        }.getOrDefault(false)
                                    }
                                }
                            }
                            .awaitAll()
                            .filter { it.second }
                            .map { it.first }
                }
    }

    data class Params(
            val includeAll: Boolean
    )
}
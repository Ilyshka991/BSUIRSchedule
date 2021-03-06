package com.pechuro.bsuirschedule.domain.repository

import com.pechuro.bsuirschedule.domain.entity.Employee
import com.pechuro.bsuirschedule.domain.entity.Group
import com.pechuro.bsuirschedule.domain.entity.Lesson
import com.pechuro.bsuirschedule.domain.entity.Schedule
import com.pechuro.bsuirschedule.domain.entity.ScheduleItem
import com.pechuro.bsuirschedule.domain.entity.ScheduleType
import com.pechuro.bsuirschedule.domain.entity.WeekNumber
import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {

    suspend fun getAllSchedules(): Flow<List<Schedule>>


    suspend fun getGroupClassesByName(name: String): Schedule.GroupClasses

    suspend fun getGroupExamsByName(name: String): Schedule.GroupExams

    suspend fun getEmployeeClassesByName(name: String): Schedule.EmployeeClasses

    suspend fun getEmployeeExamsByName(name: String): Schedule.EmployeeExams


    suspend fun getScheduleItem(schedule: Schedule, itemId: Long): Flow<ScheduleItem>

    suspend fun getScheduleItems(schedule: Schedule): Flow<List<ScheduleItem>>


    suspend fun loadGroupSchedule(group: Group, types: List<ScheduleType>): List<Schedule>

    suspend fun loadEmployeeSchedule(employee: Employee, types: List<ScheduleType>): List<Schedule>


    suspend fun isUpdateAvailable(schedule: Schedule): Boolean

    suspend fun update(schedule: Schedule)

    suspend fun updateScheduleItem(scheduleItem: ScheduleItem)

    suspend fun setNotRemindForUpdates(schedule: Schedule, notRemind: Boolean)


    suspend fun deleteSchedule(schedule: Schedule)

    suspend fun deleteScheduleItems(scheduleItems: List<ScheduleItem>)


    suspend fun addScheduleItems(schedule: Schedule, scheduleItems: List<ScheduleItem>)


    suspend fun getLessonWeeks(lesson: Lesson): List<WeekNumber>
}

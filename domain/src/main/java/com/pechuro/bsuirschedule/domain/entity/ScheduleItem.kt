package com.pechuro.bsuirschedule.domain.entity

import java.util.*

sealed class ScheduleItem(
        open val id: Long,
        open val subject: String,
        open val subgroupNumber: Int,
        open val lessonType: String,
        open val note: String,
        open val startTime: String,
        open val endTime: String,
        open val auditories: List<Auditory>
) {

    data class GroupLesson(
            override val id: Long,
            override val subject: String,
            override val subgroupNumber: Int,
            override val lessonType: String,
            override val note: String,
            override val startTime: String,
            override val endTime: String,
            override val auditories: List<Auditory>,
            val weekDay: WeekDay,
            val weekNumber: WeekNumber,
            val employees: List<Employee>
    ) : ScheduleItem(
            id = id,
            subject = subject,
            subgroupNumber = subgroupNumber,
            lessonType = lessonType,
            note = note,
            startTime = startTime,
            endTime = endTime,
            auditories = auditories
    )

    data class GroupExam(
            override val id: Long,
            override val subject: String,
            override val subgroupNumber: Int,
            override val lessonType: String,
            override val note: String,
            override val startTime: String,
            override val endTime: String,
            override val auditories: List<Auditory>,
            val date: Date,
            val employees: List<Employee>
    ) : ScheduleItem(
            id = id,
            subject = subject,
            subgroupNumber = subgroupNumber,
            lessonType = lessonType,
            note = note,
            startTime = startTime,
            endTime = endTime,
            auditories = auditories
    )

    data class EmployeeLesson(
            override val id: Long,
            override val subject: String,
            override val subgroupNumber: Int,
            override val lessonType: String,
            override val note: String,
            override val startTime: String,
            override val endTime: String,
            override val auditories: List<Auditory>,
            val weekDay: WeekDay,
            val weekNumber: WeekNumber,
            val studentGroups: List<Group>
    ) : ScheduleItem(
            id = id,
            subject = subject,
            subgroupNumber = subgroupNumber,
            lessonType = lessonType,
            note = note,
            startTime = startTime,
            endTime = endTime,
            auditories = auditories
    )

    data class EmployeeExam(
            override val id: Long,
            override val subject: String,
            override val subgroupNumber: Int,
            override val lessonType: String,
            override val note: String,
            override val startTime: String,
            override val endTime: String,
            override val auditories: List<Auditory>,
            val date: Date,
            val studentGroups: List<Group>
    ) : ScheduleItem(
            id = id,
            subject = subject,
            subgroupNumber = subgroupNumber,
            lessonType = lessonType,
            note = note,
            startTime = startTime,
            endTime = endTime,
            auditories = auditories
    )
}
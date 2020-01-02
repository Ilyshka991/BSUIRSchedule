package com.pechuro.bsuirschedule.domain.repository

import com.pechuro.bsuirschedule.domain.entity.Department
import com.pechuro.bsuirschedule.domain.entity.Faculty
import com.pechuro.bsuirschedule.domain.entity.Speciality
import kotlinx.coroutines.flow.Flow

interface ISpecialityRepository {

    suspend fun getAllFaculties(): Flow<List<Faculty>>

    suspend fun getAllDepartments(): Flow<List<Department>>

    suspend fun getAllSpecialities(): Flow<List<Speciality>>

    suspend fun update()

    suspend fun isStored(): Boolean
}
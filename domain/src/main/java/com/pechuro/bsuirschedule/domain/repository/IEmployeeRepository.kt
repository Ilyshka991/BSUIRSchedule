package com.pechuro.bsuirschedule.domain.repository

import com.pechuro.bsuirschedule.domain.entity.Employee
import kotlinx.coroutines.flow.Flow

interface IEmployeeRepository {

    suspend fun getAll(): Flow<List<Employee>>

    suspend fun getById(id: Long): Employee

    suspend fun isCached(): Boolean

    suspend fun updateCache()
}

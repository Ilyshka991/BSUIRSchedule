package com.pechuro.bsuirschedule.data.repository

import com.pechuro.bsuirschedule.data.common.BaseRepository
import com.pechuro.bsuirschedule.data.mappers.toDatabaseEntity
import com.pechuro.bsuirschedule.data.mappers.toDomainEntity
import com.pechuro.bsuirschedule.domain.entity.Employee
import com.pechuro.bsuirschedule.domain.exception.DataSourceException
import com.pechuro.bsuirschedule.domain.repository.IEmployeeRepository
import com.pechuro.bsuirschedule.domain.repository.ISpecialityRepository
import com.pechuro.bsuirschedule.local.dao.EmployeeDao
import com.pechuro.bsuirschedule.remote.api.StaffApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class EmployeeRepositoryImpl(
        private val dao: EmployeeDao,
        private val api: StaffApi,
        private val specialityRepository: ISpecialityRepository
) : BaseRepository(), IEmployeeRepository {

    override suspend fun getAll(): Flow<List<Employee>> {
        if (!isCached()) {
            updateCache()
        }
        return getEmployeesFromDao()
    }

    override suspend fun getAllNames(): Flow<List<String>> {
        return performDaoCall { dao.getAllNames() }
    }

    override suspend fun getById(id: Long): Employee {
        val employeeCached = performDaoCall { dao.getById(id) }
        val department = specialityRepository.getDepartmentById(employeeCached.departmentId)
        return employeeCached.toDomainEntity(department)
    }

    override suspend fun updateCache() {
        val loadedEmployees = loadEmployeesFromApi()
        storeEmployees(loadedEmployees)
    }

    override suspend fun deleteAll() {
        performDaoCall { dao.deleteAll() }
    }

    override suspend fun isCached(): Boolean =
            performDaoCall { dao.isNotEmpty() }

    private suspend fun loadEmployeesFromApi(): List<Employee> {
        val allDepartments = specialityRepository.getAllDepartments().first()
        return performApiCall { api.getAllEmployees() }
                .map { dto ->
                    val department = allDepartments.find {
                        it.abbreviation == dto.departmentAbbreviation.firstOrNull()
                    } ?: throw DataSourceException.InvalidData
                    dto.toDomainEntity(
                            department = department
                    )
                }
    }


    private suspend fun getEmployeesFromDao() = performDaoCall { dao.getAll() }
            .map { cachedList ->
                cachedList.map { employeeCached ->
                    val department = specialityRepository.getDepartmentById(employeeCached.departmentId)
                    employeeCached.toDomainEntity(department)
                }
            }

    private suspend fun storeEmployees(employees: List<Employee>) {
        employees.map {
            it.toDatabaseEntity()
        }.run {
            performDaoCall { dao.insertOrUpdate(this) }
        }
    }
}
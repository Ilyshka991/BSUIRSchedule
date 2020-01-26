package com.pechuro.bsuirschedule.data.repository

import com.pechuro.bsuirschedule.data.common.BaseRepository
import com.pechuro.bsuirschedule.data.mappers.toDatabaseEntity
import com.pechuro.bsuirschedule.data.mappers.toDomainEntity
import com.pechuro.bsuirschedule.domain.entity.Auditory
import com.pechuro.bsuirschedule.domain.entity.AuditoryType
import com.pechuro.bsuirschedule.domain.repository.IBuildingRepository
import com.pechuro.bsuirschedule.domain.repository.ISpecialityRepository
import com.pechuro.bsuirschedule.local.dao.BuildingDao
import com.pechuro.bsuirschedule.remote.api.BuildingApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class BuildingRepositoryImpl(
        private val dao: BuildingDao,
        private val api: BuildingApi,
        private val specialityRepository: ISpecialityRepository
) : BaseRepository(), IBuildingRepository {

    override suspend fun getAllAuditories(forceUpdate: Boolean): Flow<List<Auditory>> {
        withContext(coroutineContext) {
            launch {
                if (forceUpdate || !isCached()) {
                    updateCache()
                }
            }
        }
        return getAllAuditoriesFromDao()
    }

    override suspend fun getAllAuditoryTypes(): Flow<List<AuditoryType>> =
            performDaoCall { dao.getAllAuditoryTypes() }
                    .map { cachedList ->
                        cachedList.map { auditoryTypeCached ->
                            auditoryTypeCached.toDomainEntity()
                        }
                    }

    override suspend fun updateCache() {
        val loadedAuditories = loadAuditoriesFromApi()
        storeAuditories(loadedAuditories)
    }

    override suspend fun isCached(): Boolean =
            performDaoCall { dao.isAuditoriesNotEmpty() }

    private suspend fun loadAuditoriesFromApi(): List<Auditory> =
            performApiCall { api.getAllAuditories() }
                    .map { dto ->
                        dto.toDomainEntity()
                    }

    private suspend fun getAllAuditoriesFromDao(): Flow<List<Auditory>> =
            performDaoCall { dao.getAllAuditories() }
                    .map {
                        it.map { auditoryCached ->
                            val auditoryType = performDaoCall { dao.getAuditoryTypeById(auditoryCached.auditoryTypeId) }
                            val building = performDaoCall { dao.getBuildingById(auditoryCached.buildingId) }
                            val department = auditoryCached.departmentId?.let { departmentId ->
                                specialityRepository.getDepartmentById(departmentId)
                            }
                            auditoryCached.toDomainEntity(
                                    building = building.toDomainEntity(),
                                    auditoryType = auditoryType.toDomainEntity(),
                                    department = department
                            )
                        }
                    }

    private suspend fun storeAuditories(auditories: List<Auditory>) {
        auditories.forEach {
            it.department?.let { department ->
                specialityRepository.add(department)
            }
            val auditoryTypeCached = it.auditoryType.toDatabaseEntity()
            val departmentCached = it.department?.toDatabaseEntity()
            val buildingCached = it.building.toDatabaseEntity()
            val auditoryCached = it.toDatabaseEntity(
                    auditoryType = auditoryTypeCached,
                    building = buildingCached,
                    department = departmentCached
            )
            performDaoCall {
                dao.insert(auditoryTypeCached)
                dao.insert(buildingCached)
                dao.insert(auditoryCached)
            }
        }
    }
}
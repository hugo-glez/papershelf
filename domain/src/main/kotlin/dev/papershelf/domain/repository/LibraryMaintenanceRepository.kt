package dev.papershelf.domain.repository

interface LibraryMaintenanceRepository {
    suspend fun clearLibraryData()
}

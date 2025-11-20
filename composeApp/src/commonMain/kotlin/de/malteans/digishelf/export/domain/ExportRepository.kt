package de.malteans.digishelf.export.domain

interface ExportRepository {

    suspend fun export(): String

    /**
     * @return A Result containing a Pair of number of imported books and imported series
     */
    suspend fun import(jsonString: String): Result<Pair<Int, Int>>
}
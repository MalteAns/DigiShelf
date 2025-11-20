package de.malteans.digishelf.export.data

import de.malteans.digishelf.core.data.database.BookDao
import de.malteans.digishelf.export.data.dto.ExportDto
import de.malteans.digishelf.export.data.mappers.toEntity
import de.malteans.digishelf.export.data.mappers.toExportDto
import de.malteans.digishelf.export.domain.ExportRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class DefaultExportRepository(
    private val dao: BookDao
): ExportRepository {

    override suspend fun export(): String {
        val books = dao.queryBooks().first()
        val series = dao.querySeries().first()

        val exportDto = ExportDto(
            books = books.map { it.toExportDto() },
            series = series.map { it.toExportDto() },
        )

        return Json.encodeToString(exportDto)
    }

    override suspend fun import(jsonString: String): Result<Pair<Int, Int>> {
        return try {
            val exportDto = Json.decodeFromString<ExportDto>(jsonString)

            exportDto.series.forEach { series ->
                dao.upsertSeries(series.toEntity())
            }
            exportDto.books.forEach { book ->
                // TODO: Ensure that the bookSeriesId exists in the database
                dao.upsertBook(book.toEntity())
            }

            Result.success(Pair(exportDto.books.size, exportDto.series.size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package de.malteans.digishelf.export.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExportDto(
    val books: List<BookExportDto>,
    val series: List<SeriesExportDto>,
)

package de.malteans.digishelf.export.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeriesExportDto (
    val id: Long,
    val title: String,
    val description: String = "",
)

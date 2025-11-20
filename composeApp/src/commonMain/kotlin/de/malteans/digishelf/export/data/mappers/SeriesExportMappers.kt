package de.malteans.digishelf.export.data.mappers

import de.malteans.digishelf.core.data.database.entities.BookSeriesEntity
import de.malteans.digishelf.export.data.dto.SeriesExportDto

fun BookSeriesEntity.toExportDto() = SeriesExportDto(
    id = this.id,
    title = this.title,
    description = this.description,
)

fun SeriesExportDto.toEntity() = BookSeriesEntity(
    id = this.id,
    title = this.title,
    description = this.description,
)


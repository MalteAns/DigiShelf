package de.malteans.digishelf.core.data.mappers

import de.malteans.digishelf.core.data.database.entities.TropeEntity
import de.malteans.digishelf.core.domain.Trope

fun TropeEntity.toDomain(): Trope {
    return Trope(
        id = this.id,
        name = this.name,
    )
}

fun Trope.toEntity(): TropeEntity {
    return TropeEntity(
        id = this.id,
        name = this.name,
    )
}
package de.malteans.digishelf.export.presentation

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.writeString
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
suspend fun saveExport(
    exportContent: String,
): Result<Unit> {
    val currentLocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val file = FileKit.openFileSaver(
        suggestedName = "DigiShelf-export_${currentLocalDate.year}-${currentLocalDate.month.number}-${currentLocalDate.day}",
        extension = "json",
    ) ?: return Result.failure(Exception("File save cancelled"))
    file.writeString(exportContent)
    return Result.success(Unit)
}
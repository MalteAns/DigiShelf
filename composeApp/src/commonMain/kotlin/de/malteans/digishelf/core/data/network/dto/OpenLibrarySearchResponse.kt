package de.malteans.digishelf.core.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenLibrarySearchResponse(
    @SerialName("numFound") val numFound: Int = 0,
    @SerialName("docs") val docs: List<OpenLibraryDoc>? = null
)

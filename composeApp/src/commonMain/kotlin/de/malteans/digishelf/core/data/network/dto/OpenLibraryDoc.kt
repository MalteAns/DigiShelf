package de.malteans.digishelf.core.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenLibraryDoc(
    @SerialName("key") val key: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("subtitle") val subtitle: String? = null,
    @SerialName("author_name") val authorName: List<String>? = null,
    @SerialName("isbn") val isbn: List<String>? = null,
    @SerialName("number_of_pages_median") val numberOfPagesMedian: Int? = null,
    @SerialName("number_of_pages") val numberOfPages: Int? = null,
    @SerialName("cover_i") val coverI: Long? = null,
    @SerialName("cover_edition_key") val coverEditionKey: String? = null,
    @SerialName("first_publish_year") val firstPublishYear: Int? = null
)

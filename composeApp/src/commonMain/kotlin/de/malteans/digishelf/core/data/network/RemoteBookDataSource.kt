package de.malteans.digishelf.core.data.network

import de.malteans.digishelf.core.data.network.dto.OpenLibrarySearchResponse
import de.malteans.digishelf.core.domain.errorHandling.DataError
import de.malteans.digishelf.core.domain.errorHandling.Result

interface RemoteBookDataSource {

    suspend fun fetchBook(
        isbn: String? = null,
        title: String? = null,
        author: String? = null,
        maxResults: Int = 10
    ): Result<OpenLibrarySearchResponse, DataError.Remote>
}

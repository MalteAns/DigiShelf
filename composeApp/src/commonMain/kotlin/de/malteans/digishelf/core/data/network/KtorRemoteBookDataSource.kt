package de.malteans.digishelf.core.data.network

import de.malteans.digishelf.core.data.network.dto.OpenLibrarySearchResponse
import de.malteans.digishelf.core.domain.errorHandling.DataError
import de.malteans.digishelf.core.domain.errorHandling.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL = "https://openlibrary.org"

class KtorRemoteBookDataSource(
    private val client: HttpClient
) : RemoteBookDataSource {

    override suspend fun fetchBook(
        isbn: String?, title: String?, author: String?, maxResults: Int
    ): Result<OpenLibrarySearchResponse, DataError.Remote> {
        if (isbn == null && title == null && author == null) {
            return Result.Error(DataError.Remote.INVALIDE_QUERY)
        }

        return safeCall<OpenLibrarySearchResponse> {
            client.get("$BASE_URL/search.json") {
                if (isbn != null) {
                    parameter("isbn", isbn)
                }
                if (title != null) {
                    parameter("title", title)
                }
                if (author != null) {
                    parameter("author", author)
                }
                parameter("limit", maxResults)
            }
        }
    }
}

package de.malteans.digishelf.core.data.network

import de.malteans.digishelf.core.data.network.dto.BookResponse
import de.malteans.digishelf.core.domain.errorHandling.DataError
import de.malteans.digishelf.core.domain.errorHandling.Result
import io.ktor.client.*
import io.ktor.client.request.*

private const val BASE_URL = "https://www.googleapis.com/books/v1"

class KtorRemoteBookDataSource(
    private val client: HttpClient,
    private val apiConfig: ApiConfig
) : RemoteBookDataSource {

    override suspend fun fetchBook(
        isbn: String?, title: String?, author: String?
    ): Result<BookResponse, DataError.Remote> {
        val query = when {
            isbn != null -> "isbn:$isbn"
            title != null && author != null -> "intitle:$title+inauthor:$author"
            title != null -> "intitle:$title"
            author != null -> "inauthor:$author"
            else -> return Result.Error(DataError.Remote.INVALIDE_QUERY)
        }

        return safeCall<BookResponse> {
            client.get("$BASE_URL/volumes") {
                parameter("key", apiConfig.googleApiToken)
                parameter("q", query)
            }
        }
    }
}

package de.malteans.legal.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

//@Composable
//fun WebViewContent(
//    fileName: String,
//    modifier: Modifier = Modifier,
//) {
//    var htmlData by remember { mutableStateOf<String?>(null) }
//    LaunchedEffect(Unit) {
//        if (htmlData == null) {
//            htmlData = Res.readBytes("files/$fileName").decodeToString()
//        }
//    }
//    WebViewContent(
//        htmlData = htmlData,
//        modifier = modifier,
//    )
//}

@Composable
fun WebViewContent(
    htmlData: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        htmlData?.let { htmlData ->
            val webViewState = rememberWebViewStateWithHTMLData(
                data =  """
                        <head>
                            <style>
                                body {
                                    color: #${MaterialTheme.colorScheme.onSurface.toArgb().toHexString().takeLast(6)};
                                }
                            </style>
                        </head>
                    """.trimIndent() + htmlData,
                mimeType = "text/html",
            )
            webViewState.webSettings.apply {
                logSeverity = KLogSeverity.Debug
            }
            WebView(
                state = webViewState,
                modifier = Modifier
                    .matchParentSize()
            )
        } ?: CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}
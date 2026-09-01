package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.add_cover_image
import digishelf.composeapp.generated.resources.back
import digishelf.composeapp.generated.resources.book_cover
import digishelf.composeapp.generated.resources.edit
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlurredImageBackground(
    imageUrl: String?,
    onBackClick: () -> Unit,
    scrollState: ScrollState,
    rightIcons: @Composable () -> Unit,
    onImageClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    content: @Composable () -> Unit
) {
    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }
    var isImageLoading by remember { mutableStateOf(false) }
    
    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        onLoading = { 
            isImageLoading = true
            imageLoadResult = null
        },
        onSuccess = {
            val size = it.painter.intrinsicSize
            isImageLoading = false
            imageLoadResult = if(size.width > 1 && size.height > 1) {
                Result.success(it.painter)
            } else {
                Result.failure(Exception("Invalid image dimensions"))
            }
        },
        onError = {
            isImageLoading = false
            it.result.throwable.printStackTrace()
            imageLoadResult = Result.failure(it.result.throwable)
        }
    )
    
    val hasValidImage by remember(imageLoadResult) { derivedStateOf { imageLoadResult?.isSuccess == true } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        val contentWithScroll = @Composable {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    if (hasValidImage) {
                        Image(
                            painter = painter,
                            contentDescription = stringResource(Res.string.book_cover),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f),
                                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
                                        MaterialTheme.colorScheme.surfaceContainer
                                    )
                                )
                            )
                    ) {
                        if (onImageClick == null) return@Box
                        if (hasValidImage) return@Box

                        val addCoverButtonScale = Animatable(0f)

                        LaunchedEffect(isEditing) {
                            addCoverButtonScale.animateTo(
                                if (isEditing) 1f else 0f,
                                animationSpec = tween(durationMillis = 400, easing = EaseOutBack)
                            )
                        }

                        OutlinedButton(
                            onClick = onImageClick,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 72.dp)
                                .graphicsLayer {
                                    scaleX = addCoverButtonScale.value
                                    scaleY = addCoverButtonScale.value
                                }
                        ) {
                            Text(
                                text = stringResource(Res.string.add_cover_image),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .absoluteOffset(y = (-120).dp)
                ) {
                    if (hasValidImage) {
                        val transition = remember { Animatable(0f) }
                        LaunchedEffect(Unit) {
                            transition.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                            )
                        }
                        ElevatedCard(
                            onClick = onImageClick ?: {},
                            enabled = onImageClick != null,
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .height(230.dp)
                                .aspectRatio(2 / 3f)
                                .graphicsLayer {
                                    rotationX = (1f - transition.value) * 20f
                                    val scale = 0.7f + (0.3f * transition.value)
                                    scaleX = scale
                                    scaleY = scale
                                }
                        ) {
                            Box {
                                Image(
                                    painter = painter,
                                    contentDescription = stringResource(Res.string.book_cover),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent)
                                )
                                if (isEditing) {
                                    val alpha = remember { Animatable(0f) }
                                    LaunchedEffect(Unit) {
                                        alpha.animateTo(1f)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer { this.alpha = alpha.value }
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = stringResource(Res.string.edit),
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    content()
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        contentWithScroll()

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .statusBarsPadding()
        ) {
            val backgroundAlpha = remember { Animatable(0f) }
            LaunchedEffect(scrollState.canScrollBackward) {
                if (scrollState.canScrollBackward) {
                    backgroundAlpha.animateTo(0.5f)
                } else {
                    backgroundAlpha.animateTo(0f)
                }
            }
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = backgroundAlpha.value }
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .statusBarsPadding()
        ) {
            rightIcons()
        }
    }
}

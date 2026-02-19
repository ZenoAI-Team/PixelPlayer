package com.theveloper.pixelplay.presentation.components

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.theveloper.pixelplay.R

@Composable
fun SmartImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholderResId: Int = R.drawable.ic_music_placeholder,
    errorResId: Int = R.drawable.ic_music_placeholder,
    shape: Shape = RectangleShape,
    contentScale: ContentScale = ContentScale.Crop,
    crossfadeDurationMillis: Int = 300,
    useDiskCache: Boolean = true,
    useMemoryCache: Boolean = true,
    allowHardware: Boolean = false,
    targetSize: Size = Size(300, 300),
    colorFilter: ColorFilter? = null,
    alpha: Float = 1f,
    placeholderModel: Any? = null,
    onState: ((AsyncImagePainter.State) -> Unit)? = null
) {
    val context = LocalContext.current
    val clippedModifier = modifier.clip(shape)

    @Suppress("NAME_SHADOWING")
    val dataModel = when (model) {
        is ImageRequest -> model.data
        else -> model
    }

    if (dataModel is ImageVector || dataModel is Painter || dataModel is ImageBitmap || dataModel is Bitmap) {
        handleDirectModel(dataModel, clippedModifier, contentDescription, contentScale, colorFilter, alpha)
        return
    }

    val request = remember(context, model, crossfadeDurationMillis, useDiskCache, useMemoryCache, allowHardware, targetSize) {
        when (model) {
            is ImageRequest -> model
            else -> ImageRequest.Builder(context)
                .data(model)
                .crossfade(crossfadeDurationMillis)
                .diskCachePolicy(if (useDiskCache) CachePolicy.ENABLED else CachePolicy.DISABLED)
                .memoryCachePolicy(if (useMemoryCache) CachePolicy.ENABLED else CachePolicy.DISABLED)
                .allowHardware(allowHardware)
                .size(targetSize)
                .build()
        }
    }

    val painter = rememberAsyncImagePainter(
        model = request,
        contentScale = contentScale,
        onState = onState
    )
    val state = painter.state

    // Bolt Optimization: Key the cache to the model to prevent state leakage during LazyColumn recycling.
    // Also use LaunchedEffect to update state safely outside of the composition phase.
    var lastSuccessPainter by remember(dataModel) { mutableStateOf<Painter?>(null) }
    LaunchedEffect(state) {
        if (state is AsyncImagePainter.State.Success) {
            lastSuccessPainter = state.painter
        }
    }

    Box(modifier = clippedModifier, contentAlignment = Alignment.Center) {
        // Optimized Image rendering avoiding SubcomposeAsyncImage
        val displayPainter = when {
            state is AsyncImagePainter.State.Success -> state.painter
            lastSuccessPainter != null -> lastSuccessPainter
            else -> null
        }

        if (displayPainter != null) {
            Image(
                painter = displayPainter,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                colorFilter = colorFilter,
                alpha = alpha
            )
        } else {
            when (state) {
                is AsyncImagePainter.State.Loading, AsyncImagePainter.State.Empty -> {
                    if (placeholderModel != null) {
                        SmartImage(
                            model = placeholderModel,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = contentScale,
                            colorFilter = colorFilter,
                            alpha = alpha,
                            placeholderResId = placeholderResId
                        )
                    } else if (placeholderResId != 0) {
                        Placeholder(Modifier.fillMaxSize(), placeholderResId, contentDescription, MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurfaceVariant, alpha)
                    }
                }
                is AsyncImagePainter.State.Error -> {
                    if (errorResId != 0) {
                        Placeholder(Modifier.fillMaxSize(), errorResId, contentDescription, MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurfaceVariant, alpha)
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun handleDirectModel(
    data: Any?,
    modifier: Modifier,
    contentDescription: String?,
    contentScale: ContentScale,
    colorFilter: ColorFilter?,
    alpha: Float
) {
    when (data) {
        is ImageVector -> Image(data, contentDescription, modifier, contentScale = contentScale, colorFilter = colorFilter, alpha = alpha)
        is Painter -> Image(data, contentDescription, modifier, contentScale = contentScale, colorFilter = colorFilter, alpha = alpha)
        is ImageBitmap -> Image(data, contentDescription, modifier, contentScale = contentScale, colorFilter = colorFilter, alpha = alpha)
        is Bitmap -> Image(data.asImageBitmap(), contentDescription, modifier, contentScale = contentScale, colorFilter = colorFilter, alpha = alpha)
    }
}

@Composable
private fun Placeholder(
    modifier: Modifier,
    @DrawableRes drawableResId: Int,
    contentDescription: String?,
    containerColor: Color,
    iconColor: Color,
    alpha: Float,
) {
    Box(
        modifier = modifier.alpha(alpha).background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(drawableResId),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )
    }
}

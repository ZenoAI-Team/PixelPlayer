package com.theveloper.pixelplay.presentation.components.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun WaveformScrubber(
    amplitudes: FloatArray,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
    glowColor: Color = activeColor.copy(alpha = 0.5f),
    energy: Float = 0f
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val displayProgress = if (isDragging) dragProgress else progress

    val infiniteTransition = rememberInfiniteTransition(label = "energy_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = energy * 0.4f,
        targetValue = energy * 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        isDragging = true
                        dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        tryAwaitRelease()
                        isDragging = false
                        onProgressChange(dragProgress)
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onHorizontalDrag = { change, _ ->
                        dragProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                        onProgressChange(dragProgress)
                    }
                )
            }
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val barWidth = with(density) { 3.dp.toPx() }
        val barGap = with(density) { 2.dp.toPx() }
        val totalBarWidth = barWidth + barGap
        val barCount = (width / totalBarWidth).toInt()

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (amplitudes.isEmpty()) {
                drawRect(
                    color = inactiveColor,
                    topLeft = Offset(0f, height / 2 - 1.dp.toPx()),
                    size = Size(width, 2.dp.toPx())
                )
                return@Canvas
            }

            val samples = downsample(amplitudes, barCount)
            val maxAmp = samples.maxOrNull() ?: 1f

            samples.forEachIndexed { index, amp ->
                val x = index * totalBarWidth
                val normalizedAmp = (amp / maxAmp).coerceIn(0.05f, 1f)
                val barHeight = normalizedAmp * height
                val y = (height - barHeight) / 2

                val currentXProgress = x / width
                val isPast = currentXProgress <= displayProgress
                val color = if (isPast) activeColor else inactiveColor

                if (isPast && energy > 0.1f) {
                    drawRoundRect(
                        color = glowColor.copy(alpha = pulseAlpha),
                        topLeft = Offset(x - 2.dp.toPx(), y - 2.dp.toPx()),
                        size = Size(barWidth + 4.dp.toPx(), barHeight + 4.dp.toPx()),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }

                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }

            val handleX = displayProgress * width
            drawRect(
                color = activeColor,
                topLeft = Offset(handleX - 1.dp.toPx(), 0f),
                size = Size(2.dp.toPx(), height)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, activeColor.copy(alpha = 0.5f), Color.Transparent),
                    startX = handleX - 8.dp.toPx(),
                    endX = handleX + 8.dp.toPx()
                ),
                topLeft = Offset(handleX - 8.dp.toPx(), 0f),
                size = Size(16.dp.toPx(), height)
            )
        }
    }
}

private fun downsample(amplitudes: FloatArray, targetSize: Int): FloatArray {
    if (amplitudes.size <= targetSize) return amplitudes
    val result = FloatArray(targetSize)
    val chunkSize = amplitudes.size.toFloat() / targetSize
    for (i in 0 until targetSize) {
        val start = (i * chunkSize).toInt()
        val end = ((i + 1) * chunkSize).toInt().coerceAtMost(amplitudes.size)
        var sum = 0f
        for (j in start until end) {
            sum += amplitudes[j]
        }
        result[i] = if (end > start) sum / (end - start) else 0f
    }
    return result
}

package de.malteans.digishelf.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun customIconBarcodeScanner(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "barcode_scanner",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.25f, 5.542f)
                horizontalLineToRelative(7.042f)
                verticalLineToRelative(2f)
                horizontalLineTo(4.25f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(-2f)
                close()
                moveToRelative(28.458f, 0f)
                horizontalLineToRelative(7.042f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-5f)
                horizontalLineToRelative(-5.042f)
                close()
                moveToRelative(5.042f, 26.875f)
                verticalLineToRelative(-5f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(-7.042f)
                verticalLineToRelative(-2f)
                close()
                moveToRelative(-31.5f, -5f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(5.042f)
                verticalLineToRelative(2f)
                horizontalLineTo(2.25f)
                verticalLineToRelative(-7f)
                close()
                moveToRelative(7.375f, -17.542f)
                horizontalLineToRelative(1.708f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-1.708f)
                close()
                moveToRelative(-5f, 0f)
                horizontalLineToRelative(3.333f)
                verticalLineToRelative(20.208f)
                horizontalLineTo(6.625f)
                close()
                moveToRelative(10f, 0f)
                horizontalLineTo(20f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-3.375f)
                close()
                moveToRelative(11.75f, 0f)
                horizontalLineToRelative(1.708f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-1.708f)
                close()
                moveToRelative(3.375f, 0f)
                horizontalLineToRelative(1.625f)
                verticalLineToRelative(20.208f)
                horizontalLineTo(31.75f)
                close()
                moveToRelative(-10.083f, 0f)
                horizontalLineToRelative(5.041f)
                verticalLineToRelative(20.208f)
                horizontalLineToRelative(-5.041f)
                close()
            }
        }.build()
    }
}

@Composable
fun customReadIcon(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "Glasses",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 19f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 15f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(22f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18f, 19f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14f, 15f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 15f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14f, 15f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, -2f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, 2f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.5f, 13f)
                lineTo(5f, 7f)
                curveToRelative(0.7f, -1.3f, 1.4f, -2f, 3f, -2f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(21.5f, 13f)
                lineTo(19f, 7f)
                curveToRelative(-0.7f, -1.3f, -1.5f, -2f, -3f, -2f)
            }
        }.build()
    }
}
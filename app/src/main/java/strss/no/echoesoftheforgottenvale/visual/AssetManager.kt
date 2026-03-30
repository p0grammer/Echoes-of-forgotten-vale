package strss.no.echoesoftheforgottenvale.visual

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.LruCache
import androidx.core.graphics.ColorUtils
import strss.no.echoesoftheforgottenvale.R
import kotlin.math.max
import kotlin.math.roundToInt

data class StyledLayer(
    val bitmap: Bitmap,
    val alpha: Int,
    val scale: Float
)

data class SceneAssetPack(
    val background: StyledLayer?,
    val midground: StyledLayer?,
    val symbol: StyledLayer?,
    val focus: StyledLayer?,
    val glow: StyledLayer?,
    val accentColor: Int,
    val fogDensity: Float
)

class AssetManager(private val context: Context) {

    private val options = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
        inScaled = true
    }

    private val sourceCache = object : LruCache<Int, Bitmap>(24) {
        override fun sizeOf(key: Int, value: Bitmap): Int = value.byteCount / 1024
    }

    private val styledCache = object : LruCache<String, Bitmap>(64) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    fun preload(vararg resIds: Int) {
        resIds.filter { it != 0 }.distinct().forEach(::sourceBitmap)
    }

    fun scenePack(
        backgroundResId: Int,
        focusResId: Int,
        speaker: String?,
        sceneId: String
    ): SceneAssetPack {
        val accent = when {
            speaker == "Seraphine" || speaker == "Aethelroot" -> VisualPalette.HOPE
            sceneId.contains("menu", ignoreCase = true) -> VisualPalette.HOPE
            else -> VisualPalette.TRUTH
        }
        val overlayResId = environmentOverlay(backgroundResId, sceneId)
        val symbolResId = symbolOverlay(backgroundResId, sceneId)

        return SceneAssetPack(
            background = backgroundResId.takeIf { it != 0 }?.let {
                StyledLayer(processedBitmap("bg:$it") { buildBackgroundBitmap(sourceBitmap(it)) }, 255, 1.02f)
            },
            midground = overlayResId.takeIf { it != 0 }?.let {
                StyledLayer(processedBitmap("mid:$it") { buildMidgroundBitmap(sourceBitmap(it)) }, 64, 1.01f)
            },
            symbol = symbolResId.takeIf { it != 0 }?.let {
                StyledLayer(processedBitmap("symbol:$it:$accent") { buildSilhouetteBitmap(sourceBitmap(it), accent) }, 40, 0.98f)
            },
            focus = focusResId.takeIf { it != 0 }?.let {
                StyledLayer(processedBitmap("focus:$it:$accent") { buildFocusBitmap(sourceBitmap(it), accent) }, 228, 0.9f)
            },
            glow = focusResId.takeIf { it != 0 }?.let {
                StyledLayer(processedBitmap("glow:$it:$accent") { buildGlowBitmap(sourceBitmap(it), accent) }, 56, 1.01f)
            },
            accentColor = accent,
            fogDensity = fogDensity(backgroundResId, sceneId)
        )
    }

    private fun sourceBitmap(resId: Int): Bitmap {
        return sourceCache[resId] ?: BitmapFactory.decodeResource(context.resources, resId, options)
            ?.copy(Bitmap.Config.ARGB_8888, false)
            ?.also { sourceCache.put(resId, it) }
            ?: error("Unable to decode resource $resId")
    }

    private fun processedBitmap(key: String, producer: () -> Bitmap): Bitmap {
        return styledCache[key] ?: producer().also { styledCache.put(key, it) }
    }

    private fun buildBackgroundBitmap(source: Bitmap): Bitmap {
        val blurred = fauxBlur(source, 0.32f)
        return colorizedBitmap(
            blurred,
            saturation = 0.34f,
            contrast = 0.98f,
            tintColor = VisualPalette.ENVIRONMENT,
            tintAlpha = 92
        )
    }

    private fun buildMidgroundBitmap(source: Bitmap): Bitmap {
        return colorizedBitmap(
            source,
            saturation = 0.18f,
            contrast = 1.02f,
            tintColor = VisualPalette.ENVIRONMENT,
            tintAlpha = 68
        )
    }

    private fun buildFocusBitmap(source: Bitmap, accent: Int): Bitmap {
        return colorizedBitmap(
            source,
            saturation = 0.16f,
            contrast = 1.06f,
            tintColor = ColorUtils.blendARGB(accent, VisualPalette.TRUTH, 0.72f),
            tintAlpha = 34
        )
    }

    private fun buildSilhouetteBitmap(source: Bitmap, tint: Int): Bitmap {
        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = PorterDuffColorFilter(ColorUtils.setAlphaComponent(tint, 225), PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(source, 0f, 0f, paint)
        return output
    }

    private fun buildGlowBitmap(source: Bitmap, accent: Int): Bitmap {
        val silhouette = buildSilhouetteBitmap(source, ColorUtils.setAlphaComponent(accent, 110))
        return fauxBlur(silhouette, 0.18f)
    }

    private fun colorizedBitmap(
        source: Bitmap,
        saturation: Float,
        contrast: Float,
        tintColor: Int,
        tintAlpha: Int
    ): Bitmap {
        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(
                ColorMatrix().apply {
                    setSaturation(saturation)
                    postConcat(
                        ColorMatrix(
                            floatArrayOf(
                                contrast, 0f, 0f, 0f, 0f,
                                0f, contrast, 0f, 0f, 0f,
                                0f, 0f, contrast, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f
                            )
                        )
                    )
                }
            )
        }
        canvas.drawBitmap(source, 0f, 0f, paint)

        canvas.drawColor(ColorUtils.setAlphaComponent(tintColor, tintAlpha), PorterDuff.Mode.SRC_ATOP)
        return output
    }

    private fun fauxBlur(source: Bitmap, scale: Float): Bitmap {
        val scaledWidth = max(1, (source.width * scale).roundToInt())
        val scaledHeight = max(1, (source.height * scale).roundToInt())
        val downscaled = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true)
        return Bitmap.createScaledBitmap(downscaled, source.width, source.height, true)
    }

    private fun environmentOverlay(backgroundResId: Int, sceneId: String): Int {
        return when {
            sceneId.contains("menu", ignoreCase = true) -> R.drawable.forgotten_outskirts
            sceneId.contains("mirror", ignoreCase = true) -> R.drawable.aethelroot_core
            else -> 0
        }
    }

    private fun symbolOverlay(backgroundResId: Int, sceneId: String): Int {
        val name = safeName(backgroundResId)
        return when {
            sceneId.contains("mirror", ignoreCase = true) -> R.drawable.aethelroot_core
            sceneId.contains("final", ignoreCase = true) && name.contains("dark_city") -> R.drawable.fractured_reality
            else -> 0
        }
    }

    private fun fogDensity(backgroundResId: Int, sceneId: String): Float {
        val name = safeName(backgroundResId)
        return when {
            sceneId.contains("menu", ignoreCase = true) -> 0.42f
            name.contains("forest") || name.contains("waterfall") -> 0.34f
            name.contains("battlefield") -> 0.26f
            name.contains("dark_city") || name.contains("fractured") -> 0.22f
            else -> 0.18f
        }
    }

    private fun safeName(resId: Int): String {
        return if (resId == 0) "" else context.resources.getResourceEntryName(resId)
    }
}

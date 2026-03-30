package strss.no.echoesoftheforgottenvale.visual

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlin.math.max
import kotlin.math.sin

class VisualRenderer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var assetManager: AssetManager? = null
    private var currentPack: SceneAssetPack? = null
    private var previousPack: SceneAssetPack? = null
    private var transitionProgress: Float = 1f

    private val drawRect = RectF()
    private val fogRect = RectF()

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        isDither = true
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val skyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pulsePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fogPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var skyGradient: LinearGradient? = null
    private var vignetteGradient: RadialGradient? = null
    private var transitionAnimator: ValueAnimator? = null

    fun bind(assetManager: AssetManager) {
        this.assetManager = assetManager
    }

    fun setScene(
        backgroundResId: Int,
        focusResId: Int,
        speaker: String?,
        sceneId: String
    ) {
        val manager = assetManager ?: return
        val nextPack = manager.scenePack(backgroundResId, focusResId, speaker, sceneId)
        previousPack = currentPack
        currentPack = nextPack
        if (previousPack == null) {
            transitionProgress = 1f
            invalidate()
            return
        }

        transitionAnimator?.cancel()
        transitionAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 480
            addUpdateListener {
                transitionProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val horizon = ColorUtils.blendARGB(VisualPalette.BACKGROUND, VisualPalette.ENVIRONMENT, 0.55f)
        skyGradient = LinearGradient(
            0f,
            0f,
            0f,
            h.toFloat(),
            intArrayOf(VisualPalette.BACKGROUND, horizon, VisualPalette.BACKGROUND),
            floatArrayOf(0f, 0.52f, 1f),
            Shader.TileMode.CLAMP
        )
        vignetteGradient = RadialGradient(
            w * 0.5f,
            h * 0.52f,
            max(w, h) * 0.78f,
            intArrayOf(0x00000000, 0x42000000, 0xA0000000.toInt()),
            floatArrayOf(0.50f, 0.84f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        skyPaint.shader = skyGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), skyPaint)

        val time = SystemClock.uptimeMillis() / 1000f
        previousPack?.takeIf { transitionProgress < 1f }?.let {
            drawScene(canvas, it, 1f - transitionProgress, time)
        }
        currentPack?.let {
            drawScene(canvas, it, transitionProgress, time)
            drawFog(canvas, it, time)
            if (it.symbol != null || it.accentColor != VisualPalette.TRUTH) {
                drawPulse(canvas, it, time)
            }
        }

        vignettePaint.shader = vignetteGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)

        if (visibility == VISIBLE && alpha > 0f) {
            postInvalidateOnAnimation()
        }
    }

    private fun drawScene(canvas: Canvas, pack: SceneAssetPack, alpha: Float, time: Float) {
        val breath = (sin(time * 0.52f) * 5f).toFloat()
        drawBackground(canvas, pack.background, alpha, breath)
        drawCenteredLayer(canvas, pack.midground, alpha, time, 0.58f, 0.5f, 0.18f)
        drawGlow(canvas, pack.glow, alpha, time)
        drawForeground(canvas, pack.focus, alpha, time)
        drawCenteredLayer(canvas, pack.symbol, alpha, time, 0.52f, 0.42f, 0.15f)
    }

    private fun drawBackground(canvas: Canvas, layer: StyledLayer?, alpha: Float, breath: Float) {
        val bitmap = layer?.bitmap ?: return
        bitmapPaint.alpha = (255 * alpha).toInt()
        val zoom = layer.scale + 0.012f
        val drawWidth = width * zoom
        val drawHeight = drawWidth * bitmap.height / bitmap.width.toFloat()
        val left = ((width - drawWidth) / 2f) + breath * 0.35f
        val top = ((height - drawHeight) / 2f) - 10f
        drawRect.set(left, top, left + drawWidth, top + drawHeight)
        canvas.drawBitmap(bitmap, null, drawRect, bitmapPaint)
    }

    private fun drawCenteredLayer(
        canvas: Canvas,
        layer: StyledLayer?,
        alpha: Float,
        time: Float,
        heightFraction: Float,
        verticalBias: Float,
        parallax: Float
    ) {
        val bitmap = layer?.bitmap ?: return
        bitmapPaint.alpha = (layer.alpha * alpha).toInt()
        val floatOffset = sin((time * 0.36f) + parallax) * 4f
        val drawHeight = height * heightFraction * layer.scale
        val drawWidth = drawHeight * bitmap.width / bitmap.height.toFloat()
        val left = (width - drawWidth) / 2f + floatOffset
        val top = (height * verticalBias) - (drawHeight * 0.5f)
        drawRect.set(left, top, left + drawWidth, top + drawHeight)
        canvas.drawBitmap(bitmap, null, drawRect, bitmapPaint)
    }

    private fun drawForeground(canvas: Canvas, layer: StyledLayer?, alpha: Float, time: Float) {
        val bitmap = layer?.bitmap ?: return
        bitmapPaint.alpha = (layer.alpha * alpha).toInt()
        val driftX = sin(time * 0.32f) * 4f
        val driftY = sin(time * 0.62f) * 3f
        val drawHeight = height * 0.78f * layer.scale
        val drawWidth = drawHeight * bitmap.width / bitmap.height.toFloat()
        val left = (width - drawWidth) / 2f + driftX
        val top = height - drawHeight - 12f + driftY
        drawRect.set(left, top, left + drawWidth, top + drawHeight)
        canvas.drawBitmap(bitmap, null, drawRect, bitmapPaint)
    }

    private fun drawGlow(canvas: Canvas, layer: StyledLayer?, alpha: Float, time: Float) {
        val bitmap = layer?.bitmap ?: return
        glowPaint.alpha = (layer.alpha * alpha).toInt()
        val drift = sin(time * 0.54f) * 4f
        val drawHeight = height * 0.86f * layer.scale
        val drawWidth = drawHeight * bitmap.width / bitmap.height.toFloat()
        val left = (width - drawWidth) / 2f + drift
        val top = height - drawHeight - 18f
        drawRect.set(left, top, left + drawWidth, top + drawHeight)
        canvas.drawBitmap(bitmap, null, drawRect, glowPaint)
    }

    private fun drawFog(canvas: Canvas, pack: SceneAssetPack, time: Float) {
        val density = pack.fogDensity
        val fogColor = ColorUtils.blendARGB(VisualPalette.ENVIRONMENT, VisualPalette.TRUTH, 0.16f)
        repeat(2) { index ->
            val layerAlpha = (12 + index * 8) * density
            fogPaint.color = ColorUtils.setAlphaComponent(fogColor, layerAlpha.toInt().coerceAtMost(36))
            val bandWidth = width * (0.52f + index * 0.14f)
            val bandHeight = height * (0.10f + index * 0.02f)
            val speed = 14f + (index * 8f)
            val baseY = height * (0.30f + index * 0.24f)
            for (cloud in -1..4) {
                val x = ((time * speed) + cloud * bandWidth * 0.7f) % (width + bandWidth) - bandWidth
                val y = baseY + sin((time * 0.18f) + cloud + index) * 8f
                fogRect.set(x, y, x + bandWidth, y + bandHeight)
                canvas.drawOval(fogRect, fogPaint)
            }
        }
    }

    private fun drawPulse(canvas: Canvas, pack: SceneAssetPack, time: Float) {
        val pulseRadius = width * (0.24f + (sin(time * 0.68f) * 0.008f).toFloat())
        pulsePaint.shader = RadialGradient(
            width * 0.5f,
            height * 0.40f,
            pulseRadius,
            intArrayOf(
                ColorUtils.setAlphaComponent(pack.accentColor, 28),
                ColorUtils.setAlphaComponent(pack.accentColor, 8),
                0x00000000
            ),
            floatArrayOf(0f, 0.34f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), pulsePaint)
    }
}

package io.usys.report.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class FadedOutImageView : AppCompatImageView {

    private lateinit var paint: Paint
    private lateinit var radialGradient: RadialGradient

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint = Paint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val centerX = w / 2f
        val centerY = h / 2f
        val radius = Math.max(w, h) / 2f
        val edgeColor = 0x00FFFFFF // Transparent edge
        val centerColor = 0xFFFFFFFF.toInt() // Opaque center

        radialGradient = RadialGradient(
            centerX, centerY, radius,
            intArrayOf(centerColor, centerColor, edgeColor),
            floatArrayOf(0f, 0.8f, 1f),
            Shader.TileMode.CLAMP
        )

        paint.shader = radialGradient
    }

    override fun onDraw(canvas: Canvas) {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tmpCanvas = Canvas(bmp)
        super.onDraw(tmpCanvas)
        canvas.drawBitmap(bmp, 0f, 0f, paint)
    }
}

package com.smartqrscanner

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class QrOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val borderPaint = Paint().apply {
        color = Color.parseColor("#3B82F6") // Blue 500
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeJoin = Paint.Join.ROUND
    }

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#80000000") // 50% Black
        style = Paint.Style.FILL
    }

    private var targetRect = Rect()
    private var currentRect = Rect()
    private var isTracking = false
    private var animator: ValueAnimator? = null

    // Fallback static box variables
    private var defaultBoxSize = 600

    init {
        // Initial setup for the center square
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun updateTarget(boundingBox: Rect, rotationDegrees: Int, imageWidth: Int, imageHeight: Int) {
        // Here we map the bounding box from the raw camera image to the actual View coordinates.
        // We assume rotation 90 (portrait back camera typical)
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Swap width and height if rotated
        val scaleX = viewWidth / if (rotationDegrees == 90 || rotationDegrees == 270) imageHeight else imageWidth
        val scaleY = viewHeight / if (rotationDegrees == 90 || rotationDegrees == 270) imageWidth else imageHeight

        // Note: For front camera we'd need to mirror, but we use back camera.
        val left = boundingBox.left * scaleX
        val top = boundingBox.top * scaleY
        val right = boundingBox.right * scaleX
        val bottom = boundingBox.bottom * scaleY

        val mappedRect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        
        if (!isTracking) {
            currentRect = mappedRect
            isTracking = true
            postInvalidate()
        } else {
            // Animate smoothly to the new position
            animateTo(mappedRect)
        }
    }

    fun clearTarget() {
        if (isTracking) {
            isTracking = false
            // Animate back to center
            val cx = width / 2
            val cy = height / 2
            val r = defaultBoxSize / 2
            animateTo(Rect(cx - r, cy - r, cx + r, cy + r))
        }
    }

    private fun animateTo(dest: Rect) {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 150
            val startRect = Rect(currentRect)
            addUpdateListener { anim ->
                val fraction = anim.animatedValue as Float
                currentRect.left = startRect.left + ((dest.left - startRect.left) * fraction).toInt()
                currentRect.top = startRect.top + ((dest.top - startRect.top) * fraction).toInt()
                currentRect.right = startRect.right + ((dest.right - startRect.right) * fraction).toInt()
                currentRect.bottom = startRect.bottom + ((dest.bottom - startRect.bottom) * fraction).toInt()
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cw = width
        val ch = height

        val drawRect = if (!isTracking && currentRect.isEmpty) {
            val r = defaultBoxSize / 2
            Rect(cw / 2 - r, ch / 2 - r, cw / 2 + r, ch / 2 + r)
        } else {
            currentRect
        }

        // Draw darkened background around the transparent hole
        canvas.drawRect(0f, 0f, cw.toFloat(), drawRect.top.toFloat(), bgPaint)
        canvas.drawRect(0f, drawRect.bottom.toFloat(), cw.toFloat(), ch.toFloat(), bgPaint)
        canvas.drawRect(0f, drawRect.top.toFloat(), drawRect.left.toFloat(), drawRect.bottom.toFloat(), bgPaint)
        canvas.drawRect(drawRect.right.toFloat(), drawRect.top.toFloat(), cw.toFloat(), drawRect.bottom.toFloat(), bgPaint)

        // Draw animated corner border
        val cornerLength = 60f
        
        // Top-Left
        canvas.drawLine(drawRect.left.toFloat(), drawRect.top.toFloat(), drawRect.left + cornerLength, drawRect.top.toFloat(), borderPaint)
        canvas.drawLine(drawRect.left.toFloat(), drawRect.top.toFloat(), drawRect.left.toFloat(), drawRect.top + cornerLength, borderPaint)

        // Top-Right
        canvas.drawLine(drawRect.right.toFloat(), drawRect.top.toFloat(), drawRect.right - cornerLength, drawRect.top.toFloat(), borderPaint)
        canvas.drawLine(drawRect.right.toFloat(), drawRect.top.toFloat(), drawRect.right.toFloat(), drawRect.top + cornerLength, borderPaint)

        // Bottom-Left
        canvas.drawLine(drawRect.left.toFloat(), drawRect.bottom.toFloat(), drawRect.left + cornerLength, drawRect.bottom.toFloat(), borderPaint)
        canvas.drawLine(drawRect.left.toFloat(), drawRect.bottom.toFloat(), drawRect.left.toFloat(), drawRect.bottom - cornerLength, borderPaint)

        // Bottom-Right
        canvas.drawLine(drawRect.right.toFloat(), drawRect.bottom.toFloat(), drawRect.right - cornerLength, drawRect.bottom.toFloat(), borderPaint)
        canvas.drawLine(drawRect.right.toFloat(), drawRect.bottom.toFloat(), drawRect.right.toFloat(), drawRect.bottom - cornerLength, borderPaint)
    }
}

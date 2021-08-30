package com.vertigo.andersen_homework_4.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.vertigo.andersen_homework_4.R
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


class Dial(context: Context, attrs: AttributeSet): View(context, attrs) {
    private val painter = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 10f
    }

    var firstPath = Path()
    var secondPath = Path()
    private lateinit var firstMeasure: PathMeasure
    private lateinit var secondMeasure: PathMeasure

    // colors
    private var hourArrow_color: Int
    private var minuteArrow_color: Int
    private var secondArrow_color: Int

    // sizes
    private var hourArrow_size: Int
    private var minuteArrow_size: Int
    private var secondArrow_size: Int



    init {
        context.obtainStyledAttributes(attrs, R.styleable.Dial).apply {
            try {
                hourArrow_color = getInt(R.styleable.Dial_hourArrow_color, Color.BLACK)
                minuteArrow_color = getInt(R.styleable.Dial_minuteArrow_color, Color.BLACK)
                secondArrow_color = getInt(R.styleable.Dial_secondArrow_color, Color.BLACK)
                hourArrow_size = getInt(R.styleable.Dial_hourArrow_size, 170)
                minuteArrow_size = getInt(R.styleable.Dial_minuteArrow_size, 250)
                secondArrow_size = getInt(R.styleable.Dial_secondArrow_size, 250)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawArrows(canvas)
        drawDial(canvas)
        drawBlocks(canvas)
        drawCenterPoint(canvas)
        postInvalidateDelayed(1000)
        invalidate()
    }

    /**
     * ресую не стандартный круг через drawCircle(), а как я понял, записываю точки круга,
     * чтобы по этим точкам пройтись и поставить отметки часа в drawBlocks
     */
    private fun drawDial(canvas: Canvas?) {
        canvas?.apply {
            painter.color = Color.BLACK
            painter.style = Paint.Style.STROKE
            painter.strokeWidth = 15f

            firstPath.addCircle(width.toFloat() / 2, height.toFloat() / 2, 300f, Path.Direction.CW)
            secondPath.addCircle(width.toFloat() / 2, height.toFloat() / 2, 350f, Path.Direction.CW)
        }
        firstMeasure = PathMeasure(firstPath, false)
        secondMeasure = PathMeasure(secondPath, false)
        canvas?.drawPath(firstPath, painter)
        canvas?.drawPath(secondPath, painter)
    }

    private fun drawBlocks(canvas: Canvas?) {
        // расстояние между линиями на часах для отметок часа
        val distance = secondMeasure.length / 12

        for (i in 0..12) {
            val matrix = Matrix()
            secondMeasure.getMatrix(distance * i, matrix, PathMeasure.POSITION_MATRIX_FLAG + PathMeasure.TANGENT_MATRIX_FLAG)
            canvas?.setMatrix(matrix)
            painter.strokeWidth = 25f
            canvas?.drawLine(0f, 0f, 0f, 45f, painter)
        }
    }

    private fun drawCenterPoint(canvas: Canvas?) {
        canvas?.apply {
            painter.color = Color.BLACK
            painter.style = Paint.Style.FILL_AND_STROKE
            drawCircle(10f, 355f, 5f, painter)
        }
    }

    private fun drawHourArrow(canvas: Canvas?, location: Float) {
        val corner = Math.PI * location / 30 - Math.PI / 2

        canvas?.apply {
            painter.color = hourArrow_color
            painter.style = Paint.Style.FILL
            painter.strokeWidth = 20f
            drawLine(width.toFloat() / 2, height.toFloat() / 2, (width / 2 + cos(corner) * hourArrow_size).toFloat(),
                (height / 2 + sin(corner) * hourArrow_size).toFloat(), painter)
        }
    }

    private fun drawMinutesArrow(canvas: Canvas?, location: Float) {
        val corner = Math.PI * location / 30 - Math.PI / 2

        canvas?.apply {
            painter.color = minuteArrow_color
            painter.style = Paint.Style.FILL
            painter.strokeWidth = 15f
            drawLine(width.toFloat() / 2, height.toFloat() / 2, (width / 2 + cos(corner) * minuteArrow_size).toFloat(),
                (height / 2 + sin(corner) * minuteArrow_size).toFloat(), painter)
        }
    }

    private fun drawSecondsArrow(canvas: Canvas?, location: Float) {
        val corner = Math.PI * location / 30 - Math.PI / 2

        canvas?.apply {
            painter.color = secondArrow_color
            painter.style = Paint.Style.FILL
            painter.strokeWidth = 5f
            drawLine(width.toFloat() / 2, height.toFloat() / 2, (width / 2 + cos(corner) * secondArrow_size).toFloat(),
                (height / 2 + sin(corner) * secondArrow_size).toFloat(), painter)
        }
    }

    private fun drawArrows(canvas: Canvas?) {
        val date: Calendar = Calendar.getInstance()
        var hour = date.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > 12) hour - 12 else hour

        drawHourArrow(canvas, ((date.get(Calendar.MINUTE) / 60 + hour) * 5).toFloat())
        drawMinutesArrow(canvas, date.get(Calendar.MINUTE).toFloat())
        drawSecondsArrow(canvas, date.get(Calendar.SECOND).toFloat())
    }
}

package com.gesture.detector.circlelayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.ColorRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

/**
 * Created by zeno on 2018/2/10.
 */
open class CircleLayout : RelativeLayout {

    private val mFinger: Finger = Finger()
    private var listener: OnCircleEventListener? = null
    private var mCircle: CircleCalculator? = null

    private var minDegreeThreshold = CircleCalculator.minDegreeDefault
    private var maxDegreeThreshold = CircleCalculator.maxDegreeDefault
    private var cVRadiusThreshold = CircleCalculator.cvRadiusDefault
    private var trim = CircleCalculator.trimDefault
    private var showPoints = false

    private @ColorRes
    var chosenColor: Int = Color.BLACK
    private @ColorRes
    var circumferenceColor: Int = Color.BLUE
    private @ColorRes
    var centerColor: Int = Color.GRAY
    private @ColorRes
    var pointColor: Int = Color.RED

    private lateinit var mPointsPaint: Paint
    private lateinit var mCirclePaint: Paint
    private lateinit var mCircumferencePaint: Paint
    private lateinit var mCenterPaint: Paint
    private lateinit var mChosenPaint: Paint

    constructor(context: Context) : super(context) {
        this.setWillNotDraw(false)
        initAllPaint()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.setWillNotDraw(false)
        initAllPaint()

        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleLayout,
                0, 0)

        try {
            minDegreeThreshold = a.getFloat(R.styleable.CircleLayout_minDegreeThreshold, CircleCalculator.minDegreeDefault.toFloat()).toDouble()
            maxDegreeThreshold = a.getFloat(R.styleable.CircleLayout_maxDegreeThreshold, CircleCalculator.maxDegreeDefault.toFloat()).toDouble()
            cVRadiusThreshold = a.getFloat(R.styleable.CircleLayout_cvRadiusThreshold, CircleCalculator.cvRadiusDefault.toFloat()).toDouble()
            trim = a.getFloat(R.styleable.CircleLayout_trim, CircleCalculator.trimDefault)
            showPoints = a.getBoolean(R.styleable.CircleLayout_show, false)
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.setWillNotDraw(false)
        initAllPaint()

        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleLayout,
                0, 0)

        try {
            minDegreeThreshold = a.getFloat(R.styleable.CircleLayout_minDegreeThreshold, CircleCalculator.minDegreeDefault.toFloat()).toDouble()
            maxDegreeThreshold = a.getFloat(R.styleable.CircleLayout_maxDegreeThreshold, CircleCalculator.maxDegreeDefault.toFloat()).toDouble()
            cVRadiusThreshold = a.getFloat(R.styleable.CircleLayout_cvRadiusThreshold, CircleCalculator.cvRadiusDefault.toFloat()).toDouble()
            trim = a.getFloat(R.styleable.CircleLayout_trim, CircleCalculator.trimDefault)
            showPoints = a.getBoolean(R.styleable.CircleLayout_show, false)
        } finally {
            a.recycle()
        }
    }

    private fun initPaint(color: Int): Paint {
        val p = Paint()
        p.strokeWidth = 10F
        p.color = color
        return p
    }

    private fun initAllPaint() {
        mPointsPaint = initPaint(pointColor)
        mCirclePaint = initPaint(pointColor)
        mCircumferencePaint = initPaint(circumferenceColor)
        mCircumferencePaint.style = Paint.Style.STROKE
        mCenterPaint = initPaint(centerColor)
        mCenterPaint.style = Paint.Style.STROKE
        mChosenPaint = initPaint(chosenColor)
    }

    fun setCvRadius(value: Double) {
        cVRadiusThreshold = value
    }

    fun setDegree(minDegree: Double, maxDegree: Double) {
        this.minDegreeThreshold = minDegree
        this.maxDegreeThreshold = maxDegree
    }

    fun setTrim(trim: Float) {
        this.trim = trim
    }

    fun show() {
        showPoints = true
    }

    fun hide() {
        showPoints = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (showPoints && mCircle != null) {
            if (!mFinger.getRecord().isEmpty()) {

                canvas.drawCircle(mCircle!!.getAvgCenter().x, mCircle!!.getAvgCenter().y, mCircle!!.calAvgRadius(), mCircumferencePaint)
                canvas.drawPoint(mCircle!!.getAvgCenter().x, mCircle!!.getAvgCenter().y, mCenterPaint)

                val pointArray = pointListToArray(mFinger.getRecord())

                canvas.drawPoints(pointArray, mPointsPaint)

                val pointArray2 = pointListToArray(mCircle!!.getChosenData())

                canvas.drawPoints(pointArray2, mChosenPaint)
            }
        }
    }

    private fun pointListToArray(input: List<Point>): FloatArray {
        val array = FloatArray(input.size * 2)
        var tmp = 0

        for (point in input) {
            array[tmp++] = point.x
            array[tmp++] = point.y
        }
        return array
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mFinger.clear()
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mFinger.addPoint(createPoint(event.getX(0), event.getY(0)))
                return true
            }
            MotionEvent.ACTION_UP -> {
                val data = mFinger.getRecord()
                mCircle = CircleCalculator(data, trim)
                        .setCvRadius(cVRadiusThreshold)
                        .setDegree(minDegreeThreshold, maxDegreeThreshold)

                if (listener != null) transmit(listener!!, mCircle!!)

                invalidate()
                return true
            }
        }
        return false
    }

    private fun createPoint(x: Float, y: Float): Point {
        return Point(x, y)
    }

    private fun transmit(listener: OnCircleEventListener, circle: CircleCalculator) {
        if (circle.isCircle()) listener.onIsCircle(circle.getCVRadius(), circle.getInDegree(), circle.isFullQuadrant())
        else listener.onIsNotCircle(circle.getCVRadius(), circle.getInDegree(), circle.isFullQuadrant())
    }

    interface OnCircleEventListener {
        fun onIsCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean)
        fun onIsNotCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean)
    }

    fun setOnCircleEventListener(listener: OnCircleEventListener) {
        this.listener = listener
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}
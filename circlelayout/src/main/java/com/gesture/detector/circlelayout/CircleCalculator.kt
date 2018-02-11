package zeno.com.circleview


import com.gesture.detector.circlelayout.Point
import java.util.*

/**
 * Created by zeno on 2018/2/10.
 */
class CircleCalculator(data: List<Point>, trim: Float = trimDefault) {

    companion object {
        val cvRadiusDefault = 0.3
        val minDegreeDefault = 0.5
        val maxDegreeDefault = 1.0
        val trimDefault = 0.15f
    }

    private val centerList = ArrayList<Point>()
    private val radiusList = ArrayList<Float>()

    private var avgCenter: Point
    private var avgRadius = 0f

    private var radiusSD: Float = 0f

    private var inDegree = true

    private var fullQuadrant = false
    private var chosen = ArrayList<Point>()

    private val section = 3
    private val limit = 10

    private var cVRadiusThreshold = cvRadiusDefault

    private var minDegreeThreshold = minDegreeDefault
    private var maxDegreeThreshold = maxDegreeDefault

    private val middleData = ArrayList<Point>()

    init {

        middleData.addAll(trimList(trim, data))

        avgCenter = calAvgCenter(middleData)
        avgRadius = calAvgRadius(avgCenter, middleData)

        radiusSD = calSDRadius(avgRadius, radiusList)

        fullQuadrant = isFullQuadrant(middleData)
        inDegree = isInDegree(middleData)
    }

    fun setCvRadius(value: Double): CircleCalculator {
        cVRadiusThreshold = value
        return this
    }

    fun setDegree(minDegree: Double, maxDegree: Double): CircleCalculator {
        this.minDegreeThreshold = minDegree
        this.maxDegreeThreshold = maxDegree
        inDegree = isInDegree(middleData)
        return this
    }

    private fun trimList(percent: Float, input: List<Point>): List<Point> {
        val start = (input.size * percent).toInt()
        val end = if(start > input.size - start) input.size else input.size - start
        return input.subList(start , end)
    }

    private fun calAvgCenter(data: List<Point>): Point {
        var countX = 0f
        var countY = 0f
        var count = 0
        val firstSet = data.size / section
        val secondSet = firstSet * 2

        val interval = if (firstSet > limit) firstSet / limit else 1
        for (i in 0 until firstSet step interval) {
            for (j in firstSet + 1 until secondSet step interval) {
                for (k in secondSet + 1 until data.size step interval) {
                    chosen.add(data[i])
                    chosen.add(data[j])
                    chosen.add(data[k])
                    val tmp = circumcentre(data[i], data[j], data[k])
                    if (tmp != null) {
                        countX += tmp.x
                        countY += tmp.y
                        count++
                        centerList.add(Point(tmp.x, tmp.y))
                    }
                }
            }
        }

        countX /= count
        countY /= count

        return Point(countX, countY)
    }

    private fun isFullQuadrant(data: List<Point>): Boolean {
        val firstSet = data.size / section
        val secondSet = firstSet * 2
        val interval = if (firstSet > limit) firstSet / limit else 1
        val quadrant = BooleanArray(4)

        for (i in 0 until firstSet step interval) {
            for (j in firstSet + 1 until secondSet step interval) {
                for (k in secondSet + 1 until data.size step interval) {
                    var point = moveCenter(avgCenter, data[i])
                    findQuadrant(point, quadrant)

                    point = moveCenter(avgCenter, data[j])
                    findQuadrant(point, quadrant)

                    point = moveCenter(avgCenter, data[k])
                    findQuadrant(point, quadrant)
                }
            }
        }

        return quadrant[0] && quadrant[1] && quadrant[2] && quadrant[3]
    }

    private fun findQuadrant(point: Point, quadrant: BooleanArray): Boolean {

        if (point.x > 0 && point.y > 0) {
            quadrant[0] = true
            return true
        }
        if (point.x > 0 && point.y < 0) {
            quadrant[1] = true
            return true
        }
        if (point.x < 0 && point.y > 0) {
            quadrant[2] = true
            return true
        }
        if (point.x < 0 && point.y < 0) {
            quadrant[3] = true
            return true
        }

        return false
    }

    private fun moveCenter(center: Point, input: Point): Point {
        return Point(input.x - center.x, input.y - center.y)
    }

    private fun isInDegree(input: List<Point>): Boolean {
        for (i in 0 until input.size - 2) {
            val j = i + 1
            val k = j + 1
            val degree = calDegree(input[i], input[j], input[k])
            if (degree > maxDegreeThreshold || degree < minDegreeDefault) {
                return false
            }
        }
        return true
    }

    fun getAvgCenter() = avgCenter

    fun getCenterList() = centerList

    fun calAvgRadius() = avgRadius

    fun getRadiusList() = radiusList

    fun getCVRadius() = radiusSD / avgRadius

    fun getInDegree() = inDegree

    fun isFullQuadrant() = fullQuadrant

    fun getChosenData() = chosen

    fun isCircle(): Boolean = getCVRadius() < cVRadiusThreshold &&
            getInDegree() && isFullQuadrant()

    // https://www.spaceroots.org/documents/circle/circle-fitting.pdf(page-3)
    private fun circumcentre(a: Point, b: Point, c: Point): Point? {

        val vAB = Point(b.x - a.x, b.y - a.y)
        val vBC = Point(c.x - b.x, c.y - b.y)
        val vCA = Point(a.x - c.x, a.y - c.y)
        val sqA = a.x * a.x + a.y * a.y
        val sqB = b.x * b.x + b.y * b.y
        val sqC = c.x * c.x + c.y * c.y

        val det = vBC.x * vAB.y - vAB.x * vBC.y
        return if (Math.abs(det) < 1.0e-10) {
            null
        } else Point(
                (sqA * vBC.y + sqB * vCA.y + sqC * vAB.y) / (2 * det),
                -(sqA * vBC.x + sqB * vCA.x + sqC * vAB.x) / (2 * det))
    }

    private fun calAvgRadius(center: Point, input: List<Point>): Float {
        var radius = 0.0
        for (point in input) {
            val dx = (point.x - center.x).toDouble()
            val dy = (point.y - center.y).toDouble()
            val tmpRadius = Math.sqrt(dx * dx + dy * dy)
            radius += tmpRadius
            radiusList.add(tmpRadius.toFloat())
        }
        radius /= input.size
        return radius.toFloat()
    }

    private fun calSDRadius(radius: Float, radiusList: List<Float>): Float {
        var value = radiusList.sumByDouble { Math.pow((it - radius).toDouble(), 2.0) }
        value /= radiusList.size
        value = Math.sqrt(value)
        return value.toFloat()
    }

    private fun calDegree(a: Point, b: Point, c: Point): Double {

        val vAB = Point(b.x - a.x, b.y - a.y)
        val vBC = Point(c.x - b.x, c.y - b.y)

        val ABC = (vAB.x * vBC.x) + (vAB.y * vBC.y)

        val sqA = Math.sqrt((vAB.x * vAB.x + vAB.y * vAB.y).toDouble())
        val sqB = Math.sqrt((vBC.x * vBC.x + vBC.y * vBC.y).toDouble())

        return ABC / (sqA * sqB)
    }
}
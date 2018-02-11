package com.gesture.detector.circlelayout

import java.util.*

/**
 * Created by zeno on 2018/2/10.
 */
class Finger {
    private val record: ArrayList<Point> = ArrayList()
    fun addPoint(point: Point) {
        record.add(point)
    }

    fun getRecord(): List<Point> {
        return record
    }

    fun clear() {
        record.clear()
    }
}

data class Point(val x: Float, val y: Float) {

}
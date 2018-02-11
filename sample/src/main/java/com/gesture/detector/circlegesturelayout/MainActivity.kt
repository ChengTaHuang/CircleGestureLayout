package com.gesture.detector.circlegesturelayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.gesture.detector.circlelayout.CircleLayout

class MainActivity : AppCompatActivity() {
    private lateinit var circleLayout : CircleLayout
    private lateinit var resultTv : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTv = findViewById(R.id.result_tv)
        circleLayout = findViewById(R.id.circle_layout)
        circleLayout.show()
        circleLayout.setOnCircleEventListener(object : CircleLayout.OnCircleEventListener{
            override fun onIsCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean) {
                resultTv.text = "circle $cvRadius , $inDegree , $isFullQuadrant"
            }

            override fun onIsNotCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean) {
                resultTv.text = "not circle $cvRadius , $inDegree , $isFullQuadrant"
            }
        })
    }
}

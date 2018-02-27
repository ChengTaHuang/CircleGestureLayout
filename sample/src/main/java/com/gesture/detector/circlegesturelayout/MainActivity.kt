package com.gesture.detector.circlegesturelayout

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
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
                resultTv.text = "This is a similar circle"
            }

            override fun onIsNotCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean) {
                resultTv.text = "This is not a similar circle"
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.recycler_view -> {
                val intent = Intent(this@MainActivity , RecyclerViewSample::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

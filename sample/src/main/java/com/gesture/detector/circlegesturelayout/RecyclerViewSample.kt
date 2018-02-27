package com.gesture.detector.circlegesturelayout

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gesture.detector.circlelayout.CircleLayout


class RecyclerViewSample : AppCompatActivity() {
    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mCircleLayout : CircleLayout
    private lateinit var adapter : MyAdapter
    private lateinit var mSwipeRefreshLayout : SwipeRefreshLayout
    private val loading = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_sample)

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.isEnabled = false

        mCircleLayout = findViewById(R.id.circle_layout)
        mCircleLayout.setOnCircleEventListener(object : CircleLayout.OnCircleEventListener{
            override fun onIsCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean) {
                mSwipeRefreshLayout.isRefreshing = true
                mHandler.sendEmptyMessageDelayed(loading, 1000)
            }

            override fun onIsNotCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean) {
            }

        })

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(baseContext)

        adapter = MyAdapter(baseContext)
        mRecyclerView.adapter = adapter
    }

    class MyAdapter(private var context : Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        val data = ArrayList<Int>()
        init {
            for (i in 0 .. 100) data.add(i)
        }

        fun addItem() = data.add(data.size)

        override fun getItemCount(): Int = data.size

        private fun getData(position: Int) = data[position]

        override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
            holder.render(getData(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
            val view : View = LayoutInflater.from(context).inflate(R.layout.view_item , null , false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var textView : TextView = itemView.findViewById(R.id.text_view)

            fun render(input:Int){
                textView.text = input.toString()
            }
        }
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: android.os.Message) {

            when (msg.what) {
                loading -> {
                    mSwipeRefreshLayout.isRefreshing = false
                    adapter.addItem()
                    adapter.notifyItemChanged(adapter.itemCount-1)
                }
            }
        }
    }
}

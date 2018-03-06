# CircleGestureLayout

Gesture circle detector , extends the RelativityLayout from the Android Support Library

![CircleGestureLayout Sample ](https://github.com/ChengTaHuang/CircleGestureLayout/blob/master/demo/demo.gif)

# Including in your project
-------------------------
1. setup kotlin gradle plugin in your project level `build.gradle`
```groovy
dependencies {
    ...
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:yours version"
}
```
2. import kotlin library in your app level `build.gradle`
```groovy
dependencies {
    ...
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jre7:yours version"
}
```
3. import `circlelayout` library
```groovy
compile 'com.gesture.detector.circlelayout:circlelayout:1.0.0'
```

# Usage
-------------------------

Include the `CircleLayout` in your layout.
```xml
<com.gesture.detector.circlelayout.CircleLayout
    android:id="@+id/circle_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</com.gesture.detector.circlelayout.CircleLayout>
```

In your `onCreate` or `onCreateView` , bind the CircleLayout and setting event callback

```java
circleLayout = findViewById(R.id.circle_layout)
circleLayout.setOnCircleEventListener(object : CircleLayout.OnCircleEventListener{
  override fun onIsCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean) {
    //is a similar circle
  }
  override fun onIsNotCircle(cvRadius: Float, inDegree: Boolean, isFullQuadrant: Boolean) {
    //is not a similar circle
  }
})
```

# Description
* `cvRadius` : It's coefficient of variation of radius
* `inDegree` : It's all of two adjacent point of degree between of `minDegreeThreshold` to `maxDegreeThreshold`
* `isFullQuadrant` : If all four Quadrants contains at least one point, then the answer is true. Other cases are false

# Custom attributes
* `cvRadiusThreshHold` : Float (0.0 to 1.0) Threshold of the coefficient of variation of radius
* `minDegreeThreshold` : Float (0.0 to 1.0) Threshold of the minimum of [cos(θ)](https://github.com/ChengTaHuang/CircleGestureLayout/blob/master/demo/sin_cos_table.png) of between two adjacent points
* `maxDegreeThreshold` : Float (0.0 to 1.0) Threshold of the maximum of [cos(θ)](https://github.com/ChengTaHuang/CircleGestureLayout/blob/master/demo/sin_cos_table.png) of between two adjacent points
* `trim` : Float (0.0 to 1.0) value is percent of trim the head to tail 
* `show` : Boolean (false or true) value is show or hide the result of gesture path

# How to find Circle
[Let's go my post](https://medium.com/@zenohuang07/detect-circle-gesture-android-f5189f5cec4e)

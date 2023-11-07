package coco.man.recorder

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import java.lang.Math.abs

class MyGLSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
//    private val gestureDetector = GestureDetector(context, MyGestureListener())

//    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
//
//        override fun onScroll(
//            e1: MotionEvent,
//            e2: MotionEvent,
//            distanceX: Float,
//            distanceY: Float
//        ): Boolean {
//            val deltaX = e2.x - e1.x
//            val deltaY = e2.y - e1.y
//            MyGLUtils.setAgree(0, deltaX.toInt())
//            MyGLUtils.setAgree(1, deltaY.toInt())
//            return true
//        }
//
//    }

    var mX = 0f;
    var mY = 0f;

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mX = event.x
            mY = event.y
        } else if (event.action == MotionEvent.ACTION_MOVE) {

            val deltaX = event.x - mX
            val deltaY = event.y - mY

            mX = event.x
            mY = event.y
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                MyGLUtils.setAgree(1, deltaX.toInt())
            } else {
                MyGLUtils.setAgree(0, deltaY.toInt())
            }

        }
        return true
    }
    // ...
}
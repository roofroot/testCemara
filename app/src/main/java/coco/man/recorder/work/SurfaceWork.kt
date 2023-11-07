package coco.man.recorder.work

import android.app.Activity
import android.opengl.GLSurfaceView
import android.view.Surface
import android.view.View
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import coco.man.recorder.MyRender
import coco.man.recorder.MyRender2
import coco.man.recorder.base.MyBaseWork
import coco.man.recorder.databinding.LayoutSurfaceBinding
import coco.man.recorder.render.MySurfaceRender

/**
 * coco man
 * 2023/4/19
 **/
class SurfaceWork(binding: LayoutSurfaceBinding, context: Activity) :
    MyBaseWork<LayoutSurfaceBinding>(binding, context) {
    val render:MyRender;
    val render2:MyRender2
    init {

        binding.surfaceView.setEGLContextClientVersion(2)
//        binding.surfaceView.setRenderer(MySurfaceRender({
//            binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
//            binding.surfaceView.requestRender()
//        }))
        render=MyRender({
            binding.surfaceView.requestRender()
        }, {
            binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            binding.surfaceView.requestRender()
        })
        render2=MyRender2(context)

//        binding.surfaceView.setRenderer(render)
        binding.surfaceView.setRenderer(render2)
        bindListener(binding.surfaceView)

    }

    override fun onClick(v: View?) {
        render.changeRotate(70f,50f)
        binding.surfaceView.requestRender()
    }
}
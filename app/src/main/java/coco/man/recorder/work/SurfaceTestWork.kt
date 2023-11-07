package coco.man.recorder.work

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import coco.man.recorder.MyApplication
import coco.man.recorder.R
import coco.man.recorder.base.MenuConfig
import coco.man.recorder.base.MyBaseWork
import coco.man.recorder.databinding.ActivitySurfaceTestBinding
import coco.man.recorder.databinding.ActivityVideoBinding
import coco.man.recorder.util.LogUtils

/**
 * coco man
 * 2023/3/12
 **/
class SurfaceTestWork(
    binding: ActivitySurfaceTestBinding,
    context: Activity
) :
    MyBaseWork<ActivitySurfaceTestBinding>(binding, context) {
    companion object {
        val requestCode = 1001
    }

    var cameraWork: SurfaceWork
    var path: String? = null

    init {
        var dm = MyApplication.instance.getResources().getDisplayMetrics()
        val screenWidth = dm.widthPixels
        cameraWork = SurfaceWork(binding.layoutSurface,context)

    }



    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onClick(v: View?) {
        v?.id?.let { id ->
            when (id) {
            }

        }
    }
}
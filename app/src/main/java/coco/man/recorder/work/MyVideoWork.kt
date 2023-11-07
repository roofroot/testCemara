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
import coco.man.recorder.databinding.ActivityVideoBinding
import coco.man.recorder.util.LogUtils

/**
 * coco man
 * 2023/3/12
 **/
class MyVideoWork(
    binding: ActivityVideoBinding,
    context: Activity
) :
    MyBaseWork<ActivityVideoBinding>(binding, context) {
    companion object {
        val requestCode = 1001
    }

    var cameraWork: VideoWork
    var path: String? = null

    init {
        val config =
            MenuConfig.Build().setAppearAnim(ObjectAnimator.ofFloat(null, "Alpha", 0.0f, 1.0f))
                .setDisappearAnim(ObjectAnimator.ofFloat(null, "Alpha", 1.0f, 0.0f)).build()
        reset()
        var dm = MyApplication.instance.getResources().getDisplayMetrics()
        val screenWidth = dm.widthPixels
        val cemaraConfig =
            CemaraConfig.Build().setFacing(CameraSelector.LENS_FACING_FRONT).setResolution(
                Size(screenWidth, screenWidth * 4 / 3)
            ).build()
        cameraWork = VideoWork(binding.layoutVideo, context, cemaraConfig)

        cameraWork.onTakePhotoFinish = { path ->

            onTakePhoto(path)
        }
        cameraWork.onTakeBitFinish={
            context.runOnUiThread({
                binding.ivFacePreview.visibility=View.VISIBLE;
                binding.ivFacePreview.setImageBitmap(it)
            })

        }
        bindListener(binding.ivTakePhoto, binding.ivCancel, binding.ivConfirm)
    }

    private fun reset() {
//        binding.ivCancel.visibility = View.GONE
//        binding.ivConfirm.visibility = View.GONE
//        binding.ivFacePreview.visibility = View.GONE
//        binding.ivTakePhoto.isEnabled = true
    }

    private fun onTakePhoto(path: String) {
        LogUtils.e("path:"+path)

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
                R.id.iv_take_photo -> {
                    cameraWork.doTakePicture()
                }
                R.id.iv_cancel -> {
                    reset()
                }
                R.id.iv_confirm -> {
                }
                else -> {

                }
            }

        }
    }
}
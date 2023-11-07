package coco.man.recorder.activity

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.impl.CameraConfig
import coco.man.recorder.MyApplication
import coco.man.recorder.base.MyBaseActivity
import coco.man.recorder.databinding.ActivityMainBinding
import coco.man.recorder.databinding.ActivityVideoBinding
import coco.man.recorder.work.MyVideoWork


/**
 * coco man
 * 2023/3/23
 **/
class VideoActivity : MyBaseActivity<ActivityVideoBinding, MyVideoWork>() {
    override fun onPrepare() {

    }

    override fun getBindingInstance(): ActivityVideoBinding {
        return ActivityVideoBinding.inflate(layoutInflater)
    }

    override fun getModelInstance(): MyVideoWork {
        return MyVideoWork(binding, this)
    }

}
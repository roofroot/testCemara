package coco.man.recorder.activity

import coco.man.recorder.base.MyBaseActivity
import coco.man.recorder.databinding.ActivitySurfaceTestBinding
import coco.man.recorder.work.SurfaceTestWork
import coco.man.recorder.work.SurfaceWork

/**
 * coco man
 * 2023/4/19
 **/
class SurfaceTestActivity : MyBaseActivity<ActivitySurfaceTestBinding, SurfaceTestWork>() {
    override fun onPrepare() {

    }

    override fun getBindingInstance(): ActivitySurfaceTestBinding {
        return ActivitySurfaceTestBinding.inflate(layoutInflater)
    }

    override fun getModelInstance(): SurfaceTestWork {
        return SurfaceTestWork(binding, this)
    }
}
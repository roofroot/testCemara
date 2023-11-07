package coco.man.recorder.work

import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector

/**
 * coco man
 * 2023/3/12
 **/
class CemaraConfig {
    private constructor()

    companion object {
        fun Build(): ConfigBuild {
            return ConfigBuild(CemaraConfig())
        }
    }

    private var mFacing = CameraSelector.LENS_FACING_BACK
    private var mRotation = Surface.ROTATION_0
    private var resolution: Size = Size(900, 1200)
    fun getFacing(): Int {
        return mFacing
    }

    fun getRotation(): Int {
        return mRotation
    }
    fun getResolution():Size{
        return resolution
    }


    class ConfigBuild(config: CemaraConfig) {
        val config = config
        fun setFacing(face: Int): ConfigBuild {
            config.mFacing = face
            return this
        }

        fun build(): CemaraConfig {
            return config
        }

        fun setRotation(rotation: Int): ConfigBuild {
            config.mRotation = rotation
            return this
        }
        fun setResolution(size:Size):ConfigBuild{
            config.resolution=size
            return this
        }
    }
}
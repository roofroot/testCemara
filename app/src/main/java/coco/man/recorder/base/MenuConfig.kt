package coco.man.recorder.base

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.ViewGroup

/**
 * coco man
 * 2023/3/12
 **/
class MenuConfig {
    private constructor()

    companion object {
        fun Build(): ConfigBuild {
            return ConfigBuild(MenuConfig())
        }
    }

    private var gravityH = Gravity.TOP
    private var gravityV = Gravity.LEFT
    private var menuWidth = ViewGroup.LayoutParams.MATCH_PARENT
    private var menuHeight = ViewGroup.LayoutParams.MATCH_PARENT
    private var animAppear = ObjectAnimator.ofFloat(null, "translationY", 400f, 0f)
    private var animDisAppear = ObjectAnimator.ofFloat(null, "translationY", 0f, 400f)
    private var newRootId = false
    private var newId: Int = -1
    fun getGravityH(): Int {
        return gravityH
    }

    fun getGravityV(): Int {
        return gravityV
    }

    fun getAppearAnim(): ObjectAnimator {
        return animAppear
    }

    fun getDisappearAnim(): ObjectAnimator {
        return animDisAppear
    }

    fun getMenuH(): Int {
        return menuHeight
    }

    fun getMenuW(): Int {
        return menuWidth
    }

    fun isNewRoot(): Boolean {
        return newRootId
    }

    fun getNewId(): Int {
        return newId
    }


    class ConfigBuild(config: MenuConfig) {
        val config = config
        fun setGravityH(gravity: Int): ConfigBuild {
            config.gravityH = gravity
            return this
        }

        fun setGravityV(gravity: Int): ConfigBuild {
            config.gravityV = gravity
            return this
        }

        fun setAppearAnim(anim: ObjectAnimator): ConfigBuild {
            config.animAppear = anim
            return this
        }

        fun setDisappearAnim(anim: ObjectAnimator): ConfigBuild {
            config.animDisAppear = anim
            return this
        }

        fun setMenuHeight(h: Int): ConfigBuild {
            config.menuHeight = h
            return this
        }

        fun setMenuWidth(w: Int): ConfigBuild {
            config.menuWidth = w
            return this
        }

        fun setNewRoot(boolean: Boolean): ConfigBuild {
            config.newRootId = boolean
            return this
        }

        fun setNewId(id: Int): ConfigBuild {
            config.newId = id
            return this
        }

        fun build(): MenuConfig {
            return config
        }


    }
}
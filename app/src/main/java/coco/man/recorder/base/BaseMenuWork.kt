package coco.man.recorder.base

import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewbinding.ViewBinding
import coco.man.recorder.R
import java.lang.ref.WeakReference


abstract class BaseMenuWork<T : ViewBinding> {
    protected val binding: T
    val weakReference: WeakReference<Activity>;
    protected var constraintLayout: ConstraintLayout
    var view: View? = null
    protected var animAppear: ObjectAnimator
    protected var animDisAppear: ObjectAnimator
    protected var menuWidth = ViewGroup.LayoutParams.MATCH_PARENT
    protected var menuHeight = ViewGroup.LayoutParams.MATCH_PARENT
    protected var gravityH = Gravity.LEFT
    protected var gravityV = Gravity.TOP
    protected var cancalOnclickSpace = true

    constructor(context: Activity, view: View?) {
        val config = MenuConfig.Build().config
        this.view = view
        weakReference = WeakReference(context)
        this.binding = getBindingInstance(context)
        binding?.root?.id = View.generateViewId()
        gravityH = config.getGravityH()
        gravityV = config.getGravityV()
        menuHeight = config.getMenuH()
        menuWidth = config.getMenuW()
        animAppear = config.getAppearAnim()
        animDisAppear = config.getDisappearAnim()
        if ((context.findViewById<ConstraintLayout>(R.id.widget_bottom_menu_root) == null)) {
            constraintLayout = ConstraintLayout(context)
            addView()
        } else {
            constraintLayout = context.findViewById(R.id.widget_bottom_menu_root)
        }
    }


    constructor(context: Activity, config: MenuConfig, view: View?) {
        this.view = view
        weakReference = WeakReference(context)
        this.binding = getBindingInstance(context)
        binding?.root?.id = View.generateViewId()
        gravityH = config.getGravityH()
        gravityV = config.getGravityV()
        menuHeight = config.getMenuH()
        menuWidth = config.getMenuW()
        animAppear = config.getAppearAnim()
        animDisAppear = config.getDisappearAnim()
        if(config.isNewRoot()){
            var id:Int=config.getNewId()
            if(id==-1){
                id=config.getNewId()
            }
            if ((context.findViewById<ConstraintLayout>(id) == null)) {
                constraintLayout = ConstraintLayout(context)
                addView()
            } else {
                constraintLayout = context.findViewById(id)
            }
        } else {
            if ((context.findViewById<ConstraintLayout>(R.id.widget_bottom_menu_root) == null)) {
                constraintLayout = ConstraintLayout(context)
                addView()
            } else {
                constraintLayout = context.findViewById(R.id.widget_bottom_menu_root)
            }
        }
    }

    fun getActivity(): Activity? {
        return weakReference.get()
    }


    protected abstract fun getBindingInstance(context: Activity): T
    private fun addView() {
        getActivity()?.let { context ->
            constraintLayout.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            addAnim(constraintLayout)
            constraintLayout.id = R.id.widget_bottom_menu_root

            if (view == null) {
                (context.window.decorView as ViewGroup).addView(constraintLayout)
            } else {
                view?.let {
                    (it.parent as ViewGroup).addView(constraintLayout)
                }
            }
        }
    }

    fun showMenu() {
        if (constraintLayout.childCount > 0) {
            return
        }
        constraintLayout.isClickable = true
        constraintLayout.setBackgroundColor(Color.parseColor("#80000000"))
        if (cancalOnclickSpace) {
            constraintLayout.setOnClickListener({
                if (constraintLayout.childCount > 0) {

                    hindMenu()
                }
            })
        }
        var cs = ConstraintSet()
        constraintLayout.addView(binding.root, menuWidth, menuHeight)
        cs.clone(constraintLayout)
        if (gravityH != Gravity.LEFT) {
            cs.connect(
                binding.root.id,
                ConstraintSet.RIGHT,
                constraintLayout.id,
                ConstraintSet.RIGHT
            )
        }
        if (gravityH != Gravity.RIGHT) {
            cs.connect(binding.root.id, ConstraintSet.LEFT, constraintLayout.id, ConstraintSet.LEFT)
        }
        if (gravityV != Gravity.TOP) {
            cs.connect(
                binding.root.id,
                ConstraintSet.BOTTOM,
                constraintLayout.id,
                ConstraintSet.BOTTOM
            )
        }
        if (gravityV != Gravity.BOTTOM) {
            cs.connect(binding.root.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP)
        }
        cs.applyTo(constraintLayout)
    }

    fun hindMenu() {
        constraintLayout.isClickable = false
        constraintLayout.setBackgroundColor(Color.parseColor("#00000000"))
        constraintLayout.removeView(binding.root)
    }

    private fun addAnim(v: ViewGroup) {

        var mLayoutTransition = LayoutTransition();
        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, getAppearingAnimation());
        mLayoutTransition.setDuration(LayoutTransition.APPEARING, 200);
        mLayoutTransition.setStartDelay(
            LayoutTransition.APPEARING,
            0
        );//源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！

        mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, getDisappearingAnimation());
        mLayoutTransition.setDuration(LayoutTransition.DISAPPEARING, 200);

        mLayoutTransition.setAnimator(
            LayoutTransition.CHANGE_APPEARING,
            getAppearingChangeAnimation()
        );
        mLayoutTransition.setDuration(200);

        mLayoutTransition.setAnimator(
            LayoutTransition.CHANGE_DISAPPEARING,
            getDisappearingChangeAnimation()
        );
        mLayoutTransition.setDuration(200);

        mLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        mLayoutTransition.setStartDelay(
            LayoutTransition.CHANGE_DISAPPEARING,
            0
        );//源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！
        mLayoutTransition.addTransitionListener(object : LayoutTransition.TransitionListener {
            override fun startTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {

            }

            override fun endTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {

            }

        });

        v.setLayoutTransition(mLayoutTransition);
    }

    @SuppressLint("ObjectAnimatorBinding")
    open protected fun getAppearingAnimation(): Animator {
        var mSet = AnimatorSet();
        mSet.playTogether(
//            ObjectAnimator.ofFloat(null, "ScaleX", 2.0f, 1.0f),
//            ObjectAnimator.ofFloat(null, "ScaleY", 2.0f, 1.0f),
//            ObjectAnimator.ofFloat(null, "Alpha", 0.0f, 1.0f),
            animAppear
        )
        return mSet;
    }

    @SuppressLint("ObjectAnimatorBinding")
    open protected fun getDisappearingAnimation(): Animator {
        var mSet = AnimatorSet();
        mSet.playTogether(
//            ObjectAnimator.ofFloat(null, "ScaleX", 1.0f, 0f),
//            ObjectAnimator.ofFloat(null, "ScaleY", 1.0f, 0f),
//            ObjectAnimator.ofFloat(null, "Alpha", 1.0f, 0.0f),
            animDisAppear
        );
        return mSet;
    }

    open protected fun getDisappearingChangeAnimation(): Animator {
        var pvhLeft = PropertyValuesHolder.ofInt("left", 0, 0);
        var pvhTop = PropertyValuesHolder.ofInt("top", 0, 0);
        var pvhRight = PropertyValuesHolder.ofInt("right", 0, 0);
        var pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 0);
        var scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0f, 1.0f);
        var scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0f, 1.0f);
        var rotate = PropertyValuesHolder.ofFloat("rotation", 0f, 0f, 0f);
        return ObjectAnimator.ofPropertyValuesHolder(
            pvhLeft,
            pvhTop,
            pvhRight,
            pvhBottom,
            scaleX,
            scaleY,
            rotate
        );
    }

    open protected fun getAppearingChangeAnimation(): Animator {
        var pvhLeft = PropertyValuesHolder.ofInt("left", 0, 0);
        var pvhTop = PropertyValuesHolder.ofInt("top", 0, 0);
        var pvhRight = PropertyValuesHolder.ofInt("right", 0, 0);
        var pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 0);
        var scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 3f, 1.0f);
        var scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 3f, 1.0f);
        return ObjectAnimator.ofPropertyValuesHolder(
            pvhLeft,
            pvhTop,
            pvhRight,
            pvhBottom,
            scaleX,
            scaleY
        );
    }

    fun removeView() {
        getActivity()?.let { context ->
            constraintLayout.removeView(binding.root)
            if (view == null) {
                (context.window.decorView as ViewGroup).removeView(constraintLayout)
            } else {
                view?.let {
                    (it.parent as ViewGroup).removeView(constraintLayout)
                }

            }
        }
    }

}

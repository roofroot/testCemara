package coco.man.recorder.base

import android.app.Activity
import android.view.View
import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference

abstract class SimpleBaseWork<T : ViewBinding?> : View.OnClickListener {
    protected var binding: T
    lateinit var weakReference: WeakReference<Activity>;

    constructor(binding: T, context: Activity) {
        this.binding = binding
        weakReference = WeakReference(context)
    }

    fun getActivity(): Activity? {
        return weakReference.get()
    }

    constructor(binding: T) {
        this.binding = binding
    }

    protected fun bindListener(vararg views: View) {
        for (view in views) {
            view.setOnClickListener(this)
        }
    }
}
package coco.man.recorder.base

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference

abstract class BaseWork<T : ViewBinding?> : View.OnClickListener {
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

    abstract fun onResume()
    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    abstract fun onStop()
    abstract fun onPause()
    abstract fun onDestroy()
    abstract fun onNewIntent(intent: Intent?)
    abstract fun onRestart()
    abstract fun onBackPressed()
    abstract fun onStart()
    abstract fun dispatchKeyEvent(event: KeyEvent)
    abstract fun onKeyDown(keyCode: Int, event: KeyEvent)
    abstract fun onKeyUp(keyCode: Int, event: KeyEvent)
    abstract fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    )

    abstract fun onAttachedToWindow()

    protected abstract fun toLoginView()
    protected fun bindListener(vararg views: View) {
        for (view in views) {
            view.setOnClickListener(this)
        }
    }
}
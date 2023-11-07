package coco.man.recorder.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.lang.ref.WeakReference

open class NoBindingBaseWork {
    lateinit var weakReference: WeakReference<Activity>;

    constructor(context: Activity) {
        weakReference = WeakReference(context)
    }

    fun getActivity(): Activity? {
        return weakReference.get()
    }

    open fun onResume() {}
    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
    open fun onStop() {}
    open fun onPause() {}
    open fun onDestroy() {}
    open fun onNewIntent(intent: Intent?) {}
    open fun onRestart() {}
    open fun onBackPressed() {}
    open fun onStart() {}
    open fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
    }

    protected fun finishAnim() {
        getActivity()?.finish()
    }

    protected fun startActivityAnim(intent: Intent?) {
        getActivity()?.startActivity(intent)

    }

    protected fun startActivityForResultAnim(intent: Intent?, requestCode: Int) {
        getActivity()?.startActivityForResult(intent, requestCode)
    }

}
package coco.man.recorder.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.Telephony.Mms.Part
import android.view.KeyEvent
import androidx.navigation.NavDestination
import androidx.viewbinding.ViewBinding

abstract class MyBaseWork<T : ViewBinding?>(binding: T, context: Activity) :
    BaseWork<T>(binding, context) {

    override fun onDestroy() {

    }

    override fun onStart() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent) {}
    override fun toLoginView() {


    }

    protected fun finishAnim() {
        getActivity()?.finish()
    }

    protected fun <T> startActivityAnim(clazz: Class<T>) {
        val intent = Intent(getActivity(), clazz)
        getActivity()?.startActivity(intent)
    }

    protected fun <T> startActivityAnim(clazz: Class<T>, bundle: Bundle) {
        val intent = Intent(getActivity(), clazz)
        intent.putExtras(bundle)
        getActivity()?.startActivity(intent)
    }

    protected fun <T> startActivityAnim(clazz: Class<T>, callback: (intent: Intent) -> Unit) {
        val intent = Intent(getActivity(), clazz)
        callback(intent)
        getActivity()?.startActivity(intent)
    }

    protected fun startActivityForResultAnim(clazz: Class<T>, requestCode: Int) {
        val intent = Intent(getActivity(), clazz)
        getActivity()?.startActivityForResult(intent, requestCode)

    }

    protected fun <T> startActivityForResultAnim(
        clazz: Class<T>,
        requestCode: Int,
        callback: (intent: Intent) -> Unit
    ) {
        val intent = Intent(getActivity(), clazz)
        callback(intent)
        getActivity()?.startActivityForResult(intent, requestCode)
    }

    override fun onNewIntent(intent: Intent?) {}
    override fun onBackPressed() {}
    override fun onAttachedToWindow() {
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent) {}
    override fun dispatchKeyEvent(event: KeyEvent) {}


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
    }

    override fun onRestart() {}
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
    }

    override fun onResume() {}
    override fun onPause() {}
    override fun onStop() {}
}

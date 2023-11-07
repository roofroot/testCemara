package coco.man.recorder

import android.app.Application
import android.content.Context
import androidx.viewbinding.BuildConfig
import coco.man.recorder.BuildConfig.APPLICATION_ID


/**
 * coco man
 * 2023/3/1
 **/
class MyApplication : Application() {
    companion object {
        lateinit var instance: Application
        var device_id: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val sharedPreferences = applicationContext.getSharedPreferences(
            APPLICATION_ID + "userinfo",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putBoolean("islogin", true)
        editor.putString("token", "d8b6efc093157c9f8d7b6d1346a5cb5c")
        editor.putString("mobile", "54545545415")
        editor.commit()

    }
}
package coco.man.recorder.work

import android.opengl.GLES20
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Parcel
import android.os.Parcelable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * coco man
 * 2023/3/23
 **/
public class SimpleRenderer() : GLSurfaceView.Renderer {


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height);
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClearColor(GL_COLOR_BUFFER_BIT.toFloat(), 225f, 0f, 0f);

    }

}

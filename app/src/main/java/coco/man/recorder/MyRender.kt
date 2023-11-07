package coco.man.recorder

import android.content.Context
import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.opengl.*
import android.util.Log
import coco.man.recorder.shape.cube
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRender(
    var listener: OnFrameAvailableListener,
    var createlistener: (sf: SurfaceTexture) -> Unit
) :
    GLSurfaceView.Renderer {
//    private val vertexShaderCode = """uniform mat4 textureTransform;
//attribute vec2 inputTextureCoordinate;
//attribute vec4 position;
//varying   vec2 textureCoordinate;
//
// void main() {
//     gl_Position = position;
//     textureCoordinate = inputTextureCoordinate;
// }"""
//    private val fragmentShaderCode = """#extension GL_OES_EGL_image_external : require
//precision mediump float;
//uniform samplerExternalOES videoTex;
//varying vec2 textureCoordinate;
//
//void main() {
//    vec4 tc = texture2D(videoTex, textureCoordinate);
//    float color = tc.r  + tc.g * 0.59 + tc.b * 0.11;
//    gl_FragColor = vec4(color,color,color,1.0);
//}
//"""
//    private var mPosBuffer: FloatBuffer? = null
//    private var mTexBuffer: FloatBuffer? = null
//    private val mPosCoordinate = floatArrayOf(-1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f)
//    private val mTexCoordinateBackRight =
//        floatArrayOf(1f, 1f, 0f, 1f, 1f, 0f, 0f, 0f) //顺时针转90并沿Y轴翻转  后摄像头正确，前摄像头上下颠倒
//    private val mTexCoordinateForntRight =
//        floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f) //顺时针旋转90  后摄像头上下颠倒了，前摄像头正确
//    var mProgram = 0
//    var mBoolean = false
//    var mSurfaceTexture: SurfaceTexture? = null
//    val mlistener = createlistener


    private val mProjectMatrix = FloatArray(16)
    private val mCameraMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mTempMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(mProjectMatrix, 0)
        Matrix.setIdentityM(mCameraMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)
        Matrix.setIdentityM(mTempMatrix, 0)
    }

//    //添加程序到ES环境中
//    private fun activeProgram() {
//        // 将程序添加到OpenGL ES环境
//        GLES30.glUseProgram(mProgram)
//        mSurfaceTexture!!.setOnFrameAvailableListener(listener)
//
//        // 获取顶点着色器的位置的句柄
//        uPosHandle = GLES30.glGetAttribLocation(mProgram, "position")
//        aTexHandle = GLES30.glGetAttribLocation(mProgram, "inputTextureCoordinate")
//        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "textureTransform")
//        mPosBuffer = convertToFloatBuffer(mPosCoordinate)
//        //        if (camera_status == 0) {
////            mTexBuffer = convertToFloatBuffer(mTexCoordinateBackRight);
////        } else {
//        mTexBuffer = convertToFloatBuffer(mTexCoordinateForntRight)
//        //        }
//        GLES30.glVertexAttribPointer(uPosHandle, 2, GLES30.GL_FLOAT, false, 0, mPosBuffer)
//        GLES30.glVertexAttribPointer(aTexHandle, 2, GLES30.GL_FLOAT, false, 0, mTexBuffer)
//        // 启用顶点位置的句柄
//        GLES30.glEnableVertexAttribArray(uPosHandle)
//        GLES30.glEnableVertexAttribArray(aTexHandle)
//    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
//        mSurfaceTexture = SurfaceTexture(createOESTextureObject())
//        //启动深度测试
//        creatProgram()
        //            mProgram = ShaderUtils.createProgram(CameraGlSurfaceShowActivity.this, "vertex_texture.glsl", "fragment_texture.glsl");
//            camera = Camera.open(camera_status);
//            try {
//                camera.setPreviewTexture(mSurfaceTexture);
//                camera.startPreview();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        mSurfaceTexture?.let {
//            mlistener.invoke(it)
//        }
//        activeProgram()
    }


    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

//        creatProgram()
//        activeProgram()
    }

    override fun onDrawFrame(gl: GL10) {
        Log.e("aaa", "afdfdfdf")

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
//        cube.cubeRotateX = cubeRotateX;
//        cube.cubeRotateY = cubeRotateY;
        //获取modelMatrix, viewMatrix, projectionMatrix
//        cube.cubeRotateX = (cube.cubeRotateX + 2) % 360
        cube().draw()
    }


    var cubeRotateX = 20f
    var cubeRotateY = 30f
    fun changeRotate(rx: Float, ry: Float) {
        cubeRotateX = rx
        cubeRotateY = ry
    }

    companion object {
        fun createOESTextureObject(): Int {
            val tex = IntArray(1)
            //生成一个纹理
            GLES30.glGenTextures(1, tex, 0)
            //将此纹理绑定到外部纹理上
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
            //设置纹理过滤参数
            GLES30.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat()
            )
            GLES30.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat()
            )
            GLES30.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES30.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
            return tex[0]
        }
    }
}
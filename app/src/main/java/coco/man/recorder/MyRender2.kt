package coco.man.recorder

import android.content.Context
import android.opengl.GLSurfaceView
import coco.man.recorder.MyGLUtils
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30
import android.util.Log
import androidx.camera.core.processing.SurfaceProcessorNode.In
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig

class MyRender2(context: Context?) : GLSurfaceView.Renderer {
    private var vertexBuffer: FloatBuffer? = null
    private var colorBuffer: FloatBuffer? = null
    private var indexBuffer: ByteBuffer? = null
    private val mGLUtils: MyGLUtils
    private var mProgramId = 0
    private var mRatio = 0f

    private val vertexShaderCode = """#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;
uniform mat4 mvpMatrix;
out vec4 vColor;
void main() {
     gl_Position  = mvpMatrix * vPosition;
     vColor = aColor;
}
"""
    private val fragmentShaderCode = """#version 300 es
precision mediump float; //声明float型变量的精度为mediump
in vec4 vColor;
out vec4 fragColor;
void main() {
     fragColor = vColor;
}

"""

    init {
        mGLUtils = MyGLUtils()
    }

    override fun onSurfaceCreated(gl: GL10, eglConfig: EGLConfig) {
        //设置背景颜色
        GLES30.glClearColor(0.1f, 0.2f, 0.3f, 0.4f)
        //启动深度测试
        gl.glEnable(GLES30.GL_DEPTH_TEST)
        //编译着色器
//        final int vertexShaderId = mGLUtils.compileShader(GLES30.GL_VERTEX_SHADER, R.raw.vertex_shader);
//        final int fragmentShaderId = mGLUtils.compileShader(GLES30.GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)
        //链接程序片段
        mProgramId = mGLUtils.linkProgram(vertexShader, fragmentShader)
        GLES30.glUseProgram(mProgramId)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES30.glCreateShader(type)
        // 添加上面编写的着色器代码并编译它
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
        return shader
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        //设置视图窗口
        GLES30.glViewport(0, 0, width, height)
        floatBuffer
        mRatio = 1.0f * width / height
    }

    override fun onDrawFrame(gl: GL10) {
        Log.e("aaa", "afdfdfdf")
        //将颜色缓冲区设置为预设的颜色
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        mGLUtils.transform(mProgramId, mRatio) //计算MVP变换矩阵
        //启用顶点的数组句柄
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glEnableVertexAttribArray(1)
        //准备顶点坐标和颜色数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        //绘制正方体的表面（6个面，每面2个三角形，每个三角形3个顶点）
        gl.glDrawElements(GLES30.GL_TRIANGLES, 6 * 2 * 3, GLES30.GL_UNSIGNED_BYTE, indexBuffer)
        //禁止顶点数组句柄
        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }

    val floatBuffer: Unit
        get() {
            val r = 1.0f
//            val vertex = floatArrayOf(
//                r, r, r,  //0
//                -r, r, r,  //1
//                -r, -r, r,  //2
//                r, -r, r,  //3
//                r, r, -r,  //4
//                -r, r, -r,  //5
//                -r, -r, -r,  //6
//                r, -r, -r //7
//            )
            val vertex = floatArrayOf(
                //前面
                r, r, r,
                -r, r, r,
                -r, -r, r,
                r, -r, r,
                //后面
                r, r, -r,
                -r, r, -r,
                -r, -r, -r,
                r, -r, -r,
                //上面
                r, r, r,
                r, r, -r,
                -r, r, -r,
                -r, r, r,
                //下面
                r, -r, r,
                r, -r, -r,
                -r, -r, -r,
                -r, -r, r,
                //右面
                r, r, r,
                r, r, -r,
                r, -r, -r,
                r, -r, r,
                //左面
                -r, r, r,
                -r, r, -r,
                -r, -r, -r,
                -r, -r, r
            );

//            val index = byteArrayOf(
//                0, 2, 1, 0, 2, 3,  //前面
//                0, 5, 1, 0, 5, 4,  //上面
//                0, 7, 3, 0, 7, 4,  //右面
//                6, 4, 5, 6, 4, 7,  //后面
//                6, 3, 2, 6, 3, 7,  //下面
//                6, 1, 2, 6, 1, 5 //左面
//            )
            val x: Int = 0;
            val y: Int = 1;
            val z: Int = 2;
            val k: Int = 3
            val index = byteArrayOf(
                x.toByte(),
                z.toByte(),
                y.toByte(),
                x.toByte(),
                z.toByte(),
                k.toByte(),  //前面
                (x + 8).toByte(),
                (z + 8).toByte(),
                (y + 8).toByte(),
                (x + 8).toByte(),
                (z + 8).toByte(),
                (k + 8).toByte(),
                (x + 16).toByte(),
                (z + 16).toByte(),
                (y + 16).toByte(),
                (x + 16).toByte(),
                (z + 16).toByte(),
                (k + 16).toByte(),
                (x + 4).toByte(),
                (z + 4).toByte(),
                (y + 4).toByte(),
                (x + 4).toByte(),
                (z + 4).toByte(),
                (k + 4).toByte(),
                (x + 12).toByte(),
                (z + 12).toByte(),
                (y + 12).toByte(),
                (x + 12).toByte(),
                (z + 12).toByte(),
                (k + 12).toByte(),
                (x + 20).toByte(),
                (z + 20).toByte(),
                (y + 20).toByte(),
                (x + 20).toByte(),
                (z + 20).toByte(),
                (k + 20).toByte(),
            )
            val c = 1.0f
//            val color = floatArrayOf(
//                c, c, c, 1f, 0f, c, c, 1f, 0f, 0f, c, 1f,
//                c, 0f, c, 1f,
//                c, c, 0f, 1f, 0f, c, 0f, 1f, 0f, 0f, 0f, 1f,
//                c, 0f, 0f, 1f
//            )
//            val color = floatArrayOf(
//                c, 0f, 0f, 1f,
//                c, 0f, 0f, 1f,
//                c, 0f, 0f, 1f,
//                c, 0f, 0f, 1f,
//
//                c, 0f, 0f, 1f,
//                c, 0f, 0f, 1f,
//                c, 0f, 0f, 1f,
//                c, 0f, 0f, 1f,
//            )
            val color = floatArrayOf(
                // Front face (red)
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,

                // Back face (green)
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,

                // Left face (blue)
                0f, 0f, 1f, 1f,
                0f, 0f, 1f, 1f,
                0f, 0f, 1f, 1f,
                0f, 0f, 1f, 1f,

                // Right face (yellow)
                1f, 1f, 0f, 1f,
                1f, 1f, 0f, 1f,
                1f, 1f, 0f, 1f,
                1f, 1f, 0f, 1f,

                // Top face (cyan)
                0f, 1f, 1f, 1f,
                0f, 1f, 1f, 1f,
                0f, 1f, 1f, 1f,
                0f, 1f, 1f, 1f,

                // Bottom face (magenta)
                1f, 0f, 1f, 1f,
                1f, 0f, 1f, 1f,
                1f, 0f, 1f, 1f,
                1f, 0f, 1f, 1f,

                )
            vertexBuffer = mGLUtils.getFloatBuffer(vertex)
            indexBuffer = mGLUtils.getByteBuffer(index)
            colorBuffer = mGLUtils.getFloatBuffer(color)
        }
}
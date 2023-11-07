package coco.man.recorder

import android.R
import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRender3(context: Context?) : GLSurfaceView.Renderer {
    private var vertexBuffer: FloatBuffer? = null
    private var colorBuffer: FloatBuffer? = null
    private var indexBuffer: ByteBuffer? = null
    private val mGLUtils: MyGLUtils
    private var mProgramId = 0
    private var mRatio = 0f

    private val vertexShaderCode = """#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;
uniform mat4 mvpMatrix;
out vec2 vTexCoord;
void main() {
     gl_Position  = mvpMatrix * vPosition;
     vTexCoord = aTextureCoord;
}

"""

    private val fragmentShaderCode = """#version 300 es
precision mediump float;
uniform sampler2D uTextureUnit;
in vec2 vTexCoord;
out vec4 fragColor;
void main() {
     fragColor = texture(uTextureUnit,vTexCoord);
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
        Log.e("aaa","afdfdfdf")
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

//            val resIds = intArrayOf(
//                R.raw.x1, R.raw.x2, R.raw.x3,
//                R.raw.x4, R.raw.x5, R.raw.x6
//            )

            val r = 1.0f
            val vertex = floatArrayOf(
                r, r, r,  //0
                -r, r, r,  //1
                -r, -r, r,  //2
                r, -r, r,  //3
                r, r, -r,  //4
                -r, r, -r,  //5
                -r, -r, -r,  //6
                r, -r, -r //7
            )
            val index = byteArrayOf(
                0, 2, 1, 0, 2, 3,  //前面
                0, 5, 1, 0, 5, 4,  //上面
                0, 7, 3, 0, 7, 4,  //右面
                6, 4, 5, 6, 4, 7,  //后面
                6, 3, 2, 6, 3, 7,  //下面
                6, 1, 2, 6, 1, 5 //左面
            )
            val c = 1.0f
//            val color = floatArrayOf(
//                c, c, c, 1f, 0f, c, c, 1f, 0f, 0f, c, 1f,
//                c, 0f, c, 1f,
//                c, c, 0f, 1f, 0f, c, 0f, 1f, 0f, 0f, 0f, 1f,
//                c, 0f, 0f, 1f
//            )
            val color = floatArrayOf(
                c, 0f, 0f, 1f, c, 0f, 0f, 1f, c, 0f, 0f, 1f,
                c, 0f, 0f, 1f,
                c, c, c, c, c, c, c, c,c, c, c, c,
                c, c, c, c
            )
            vertexBuffer = mGLUtils.getFloatBuffer(vertex)
            indexBuffer = mGLUtils.getByteBuffer(index)
            colorBuffer = mGLUtils.getFloatBuffer(color)
        }
}
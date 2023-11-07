package coco.man.recorder.render

import android.opengl.GLES20.*
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MySurfaceRender(onFrameAvailable: (() -> Unit)) :
    GLSurfaceView.Renderer {
    private val TAG = "render"

    val cubeVertices = floatArrayOf(
        // Front face
        -1.0f, -1.0f, 1.0f,  // 0
        1.0f, -1.0f, 1.0f,  // 1
        1.0f, 1.0f, 1.0f,  // 2
        -1.0f, 1.0f, 1.0f,  // 3
        // Back face
        -1.0f, -1.0f, -1.0f,  // 4
        1.0f, -1.0f, -1.0f,  // 5
        1.0f, 1.0f, -1.0f,  // 6
        -1.0f, 1.0f, -1.0f,  // 7
        // Top face
        1.0f, 1.0f, 1.0f,  // 8
        -1.0f, 1.0f, 1.0f,  // 9
        -1.0f, 1.0f, -1.0f,  // 10
        1.0f, 1.0f, -1.0f,  // 11
        // Bottom face
        -1.0f, -1.0f, 1.0f,  // 12
        1.0f, -1.0f, 1.0f,  // 13
        1.0f, -1.0f, -1.0f,  // 14
        -1.0f, -1.0f, -1.0f,  // 15
        // Right face
        1.0f, -1.0f, 1.0f,  // 16
        1.0f, -1.0f, -1.0f,  // 17
        1.0f, 1.0f, -1.0f,  // 18
        1.0f, 1.0f, 1.0f,  // 19
        // Left face
        -1.0f, -1.0f, 1.0f,  // 20
        -1.0f, 1.0f, 1.0f,  // 21
        -1.0f, 1.0f, -1.0f,  // 22
        -1.0f, -1.0f, -1.0f   // 23
    )

    val cubeIndices = shortArrayOf(
        0, 1, 2, 0, 2, 3,    // Front face
        4, 5, 6, 4, 6, 7,    // Back face
        8, 9, 10, 8, 10, 11,   // Top face
        12, 13, 14, 12, 14, 15,   // Bottom face
        16, 17, 18, 16, 18, 19,   // Right face
        20, 21, 22, 20, 22, 23    // Left face
    )

        val cubeColors= floatArrayOf(
        // Front face (red)
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        // Back face (green)
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        // Left face (blue)
        0f, 0f, 1f, 1f,
        0f, 0f, 1f, 1f,
        0f, 0f, 1f, 1f,
        0f, 0f, 1f, 1f,
        0f, 0f, 1f, 1f,
        0f, 0f, 1f, 1f,
        // Right face (yellow)
        1f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f,
        // Top face (cyan)
        0f, 1f, 1f, 1f,
        0f, 1f, 1f, 1f,
        0f, 1f, 1f, 1f,
        0f, 1f, 1f, 1f,
        0f, 1f, 1f, 1f,
        0f, 1f, 1f, 1f,
        // Bottom face (magenta)
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 1f, 1f
    )
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

    var onFrameAvailable: (() -> Unit) = onFrameAvailable
    val cubeColorBuffer =
        ByteBuffer.allocateDirect(cubeColors.size * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(cubeColors)
            .position(0)


    val cubeVertexBuffer = ByteBuffer.allocateDirect(cubeVertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(cubeVertices)
        .position(0)


    val cubeIndexBuffer = ByteBuffer.allocateDirect(cubeIndices.size * 2)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer()
        .put(cubeIndices)
        .position(0)


    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"

//    private val vertexShaderCode = """//指定版本号
//#version 300 es
//uniform mat4 transform;layout (location = 0) in vec3 aPos;
//layout (location = 1) in vec4 aColor;
//out vec4 color;
//void main()
//{
//    // gl_Position (内置函数) 赋值位置
//    gl_Position = transform*vec4(aPos, 1.0);
//    color = aColor;
//}"""


    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"

//    private val fragmentShaderCode = """#version 300 es
//precision mediump float;
//
//out vec4 fragColor;
//in vec4 color;
//void main()
//{
//    fragColor = color;
//}"""


    val program =createProgram()

    val positionHandle = glGetAttribLocation(program, "vPosition")
    val colorHandle = glGetAttribLocation(program, "vColor")
    val mMVPMatrixHandle = glGetUniformLocation(program, "textureTransform")
    val modelMatrix = FloatArray(16)

    fun drawCube() {

        glClearColor(0.6f, 0.1f, 0.3f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        glUseProgram(program)

        glEnableVertexAttribArray(positionHandle)
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, cubeVertexBuffer)
        glEnableVertexAttribArray(colorHandle)
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, cubeColorBuffer)


        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.rotateM(modelMatrix, 0, 30f, 0f, 1f, 0f);
//
        Matrix.rotateM(modelMatrix, 0, 30f, 0f, 0f, 1f);


        val transform = glGetUniformLocation(program, "transform")
        glUniformMatrix4fv(transform, 1, false, modelMatrix, 0)


        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_SHORT, cubeIndexBuffer)

//        glDrawArrays(GL_TRIANGLES, 0, 36)

        // 画三角形

        // 画三角形
//        glDrawArrays(GL_TRIANGLES, 0, 36)
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
//        glDisableVertexAttribArray(mMVPMatrixHandle)
    }

    private fun initProgram(): Int {
        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        val mProgram = glCreateProgram()
        if (mProgram !== 0) {
            glAttachShader(mProgram, vertexShader)
            glAttachShader(mProgram, fragmentShader)
            glLinkProgram(mProgram)
            val linkStatus = IntArray(1)
            glGetProgramiv(mProgram, GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GL_TRUE) {
                Log.e(TAG, "连接着色器失败 ")
                Log.e(TAG, glGetProgramInfoLog(mProgram))
                glDeleteProgram(mProgram)
                return 0
            }
        }

        return mProgram
    }


    private fun createProgram(): Int {
        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        // 创建空的OpenGL ES程序
        val mProgram = glCreateProgram()

        // 添加顶点着色器到程序中
        glAttachShader(mProgram, vertexShader)

        // 添加片段着色器到程序中
        glAttachShader(mProgram, fragmentShader)

        // 创建OpenGL ES程序可执行文件
        glLinkProgram(mProgram)

        // 释放shader资源
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
        return mProgram
    }

    //    fun loadShader(type: Int, shaderCode: String): Int {
//        val shader = glCreateShader(type)
//        // 添加上面编写的着色器代码并编译它
//        glShaderSource(shader, shaderCode)
//        glCompileShader(shader)
//        return shader
//    }
    fun loadShader(type: Int, shaderCode: String): Int {
        var shader = glCreateShader(type)
        if (shader != 0) {
            glShaderSource(shader, shaderCode)
            glCompileShader(shader)
            val compiled = IntArray(1)
            glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }


    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        onFrameAvailable.invoke()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
//        Matrix.scaleM(mMVPMatrix, 0, 1f, -1f, 1f)
//        val ratio = width.toFloat() / height
//        Matrix.orthoM(mProjectMatrix, 0, -1f, 1f, -ratio, ratio, 1f, 7f) // 3和7代表远近视点与眼睛的距离，非坐标点
//
//        Matrix.setLookAtM(mCameraMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f) // 3代表眼睛的坐标点
//
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mCameraMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES30.glClearColor(GL_COLOR_BUFFER_BIT.toFloat(), 225f, 0f, 0f);
        drawCube()
    }
}
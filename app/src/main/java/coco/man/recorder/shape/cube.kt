package coco.man.recorder.shape

import android.content.Context
import android.opengl.GLES30
import android.opengl.Matrix
import coco.man.recorder.MyGLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL

class cube() {
    // 给顶点增加 颜色属性
    private val vertexShaderCode = """attribute vec4 vPosition; 
attribute vec4 vColor; 
uniform mat4 transform; 
varying vec4 v_Color; 
void main()
{
    gl_Position = vPosition * transform;
    v_Color = vColor; 
}"""
    private val fragmentShaderCode = "precision mediump float;" +
            "varying vec4 v_Color;" +
            "uniform mat4 mColor; " +
            "void main() {" +
            "  gl_FragColor = v_Color;" +
            "}"


    val cubeIndices = shortArrayOf(
        0, 1, 2, 0, 2, 3,    // Front face
        4, 5, 6, 4, 6, 7,    // Back face
        8, 9, 10, 8, 10, 11,   // Top face
        12, 13, 14, 12, 14, 15,   // Bottom face
        16, 17, 18, 16, 18, 19,   // Right face
        20, 21, 22, 20, 22, 23    // Left face
    )

    var cubeRotateY = 0f


    val cubeVertices = floatArrayOf(
        //背面矩形
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, 0.75f, 0.0f, //V6
        -0.25f, -0.25f, 0.0f, //V7
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, -0.25f, 0.0f, //V7
        0.75f, -0.25f, 0.0f, //V4

        //左侧矩形
        -0.25f, 0.75f, 0.0f, //V6
        -0.75f, 0.25f, 0.0f, //V1
        -0.75f, -0.75f, 0.0f, //V2
        -0.25f, 0.75f, 0.0f, //V6
        -0.75f, -0.75f, 0.0f, //V2
        -0.25f, -0.25f, 0.0f, //V7

        //底部矩形
        0.75f, -0.25f, 0.0f, //V4
        -0.25f, -0.25f, 0.0f, //V7
        -0.75f, -0.75f, 0.0f, //V2
        0.75f, -0.25f, 0.0f, //V4
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, -0.75f, 0.0f, //V3

        //正面矩形
        0.25f, 0.25f, 0.0f,  //V0
        -0.75f, 0.25f, 0.0f, //V1
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, 0.25f, 0.0f,  //V0
        -0.75f, -0.75f, 0.0f, //V2
        0.25f, -0.75f, 0.0f, //V3

        //右侧矩形
        0.75f, 0.75f, 0.0f, //V5
        0.25f, 0.25f, 0.0f, //V0
        0.25f, -0.75f, 0.0f, //V3
        0.75f, 0.75f, 0.0f, //V5
        0.25f, -0.75f, 0.0f, //V3
        0.75f, -0.25f, 0.0f, //V4

        //顶部矩形
        0.75f, 0.75f, 0.0f, //V5
        -0.25f, 0.75f, 0.0f, //V6
        -0.75f, 0.25f, 0.0f, //V1
        0.75f, 0.75f, 0.0f, //V5
        -0.75f, 0.25f, 0.0f, //V1
        0.25f, 0.25f, 0.0f  //V0
    );


    // 设置颜色RGBA（red green blue alpha）
    var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f);

    val cubeColors = floatArrayOf(
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
    val cubeColorBuffer =
        ByteBuffer.allocateDirect(cubeColors.size * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer().put(cubeColors)
            .position(0)


    val cubeVertexBuffer = ByteBuffer.allocateDirect(cubeVertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(cubeVertices)
        .position(0)

    private val mProgram: Int
    private var mPositionHandle = 0
    private var mColorHandle = 0
    private val vertexCount = cubeVertices.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    private val colorStride = 4 * 4 // 4 bytes per vertex
    private val mProjectMatrix = FloatArray(16)
    private val mCameraMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mTempMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)

    init {
        // 为存放形状的坐标，初始化顶点字节缓冲
//        val bb = ByteBuffer.allocateDirect( // (坐标数 * 4 )float 占四个字节
//            cubeVertices.size * 4
//        )
//        // 使用设备的本点字节序
//        bb.order(ByteOrder.nativeOrder())
//
//        // 从ByteBuffer创建一个浮点缓冲
//        vertexBuffer = bb.asFloatBuffer()
//        // 把坐标加入FloatBuffer中
//        vertexBuffer.put(cubeVertices)
//        // 设置buffer，从第一个坐标开始读
//        vertexBuffer.position(0)
//
//
//        val cc = ByteBuffer.allocateDirect( // (坐标数 * 4 )float 占四个字节
//            cubeColors.size * 4
//        )
//        // 使用设备的本点字节序
//        cc.order(ByteOrder.nativeOrder())
//
//        // 从ByteBuffer创建一个浮点缓冲
//        colorsBuffer = cc.asFloatBuffer()
//        // 把坐标加入FloatBuffer中
//        colorsBuffer.put(cubeColors)
//        // 设置buffer，从第一个坐标开始读
//        colorsBuffer.position(0)

        Matrix.setIdentityM(mProjectMatrix, 0)
        Matrix.setIdentityM(mCameraMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)
        Matrix.setIdentityM(mTempMatrix, 0)
        Matrix.scaleM(mMVPMatrix, 0, 1f, -1f, 1f)
        Matrix.orthoM(mProjectMatrix, 0, -1f, 1f, -1f, 1f, 1f, 7f) // 3和7代表远近视点与眼睛的距离，非坐标点
        Matrix.setLookAtM(mCameraMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f) // 3代表眼睛的坐标点
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mCameraMatrix, 0)
        val vertexShader = loadShader(
            GLES30.GL_VERTEX_SHADER,
            vertexShaderCode
        )
        val fragmentShader = loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            fragmentShaderCode
        )

        // 创建一个空的 OpenGL ES Program
        mProgram = GLES30.glCreateProgram()

        // 将vertex shader 添加到 program
        GLES30.glAttachShader(mProgram, vertexShader)

        // 将fragment shader 添加到 program
        GLES30.glAttachShader(mProgram, fragmentShader)

        // 创建一个可执行的 OpenGL ES program
        GLES30.glLinkProgram(mProgram)
    }


    fun draw() {
        // 将program 添加到 OpenGL ES 环境中
        GLES30.glUseProgram(mProgram)

        // 获取指向vertex shader的成员vPosition的句柄
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition")
//        mColorHandle = GLES30.glGetUniformLocation(mProgram, "mColor")

        mColorHandle = GLES30.glGetAttribLocation(mProgram, "vColor")
        // 启用一个指向三角形的顶点数组的句柄
        GLES30.glEnableVertexAttribArray(mPositionHandle)
        //
        // 准备三角形的坐标数据
        GLES30.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES30.GL_FLOAT, false,
            0, cubeVertexBuffer
        )
        GLES30.glEnableVertexAttribArray(mColorHandle)
        GLES30.glVertexAttribPointer(
            mColorHandle, 4, GLES30.GL_FLOAT, false, 0, cubeColorBuffer
        )



        Matrix.setIdentityM(mModelMatrix, 0);

//        Matrix.rotateM(modelMatrix, 0, 30f, 0f, 1f, 0f);
//        Matrix.rotateM(modelMatrix, 0, 30f, 0f, 0f, 1f);


//        Matrix.rotateM(mModelMatrix, 0, 30f, 0f, 0f, 1f);

        cubeRotateX = (cubeRotateX + 2) % 360

//        Matrix.rotateM(mModelMatrix, 0, cubeRotateX, 0f, 0f, 1f);
        Matrix.rotateM(mModelMatrix, 0, cubeRotateX, 1f, 0f, 0f);
//        Matrix.rotateM(mModelMatrix, 0, cubeRotateX, 1f, 1f, 1f);
        // Multiply the model, view, and projection matrices
        val mMVPMatrix = FloatArray(16)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mCameraMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mModelMatrix, 0, mMVPMatrix, 0)

// Pass the model-view-projection matrix to the shader

        val mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "transform")

        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)
//        transform(mProgram,1f)
//
//        val transform = GLES30.glGetUniformLocation(mProgram, "transform")
//
//        GLES30.glUniformMatrix4fv(transform, 1, false, mModelMatrix, 0)
//        MyGLUtils().transform(mProgram,1f)

        // 绘制三角形    // 设置三角形的颜色
//        GLES30.glUniform4fv(mColorHandle, 1, color, 0);


        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36)

        // 禁用指向三角形的定点数组
        GLES30.glDisableVertexAttribArray(mPositionHandle)
//        GLES30.glDisableVertexAttribArray(mMVPMatrixHandle)
        GLES30.glDisableVertexAttribArray(mColorHandle)
    }



    private fun getIdentityMatrix(size: Int, offset: Int): FloatArray {
        val matrix = FloatArray(size)
        Matrix.setIdentityM(matrix, offset)
        return matrix
    }

    fun loadShader(type: Int, shaderCode: String?): Int {

        // 创建一个vertex shader 类型 (GLES30.GL_VERTEX_SHADER)
        // 或者一个 fragment shader 类型(GLES30.GL_FRAGMENT_SHADER)
        val shader = GLES30.glCreateShader(type)

        // 将源码添加到shader并编译
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
        return shader
    }

    companion object {
        // 数组中每个顶点的坐标数
        var cubeRotateX = 380f
        const val COORDS_PER_VERTEX = 3
    }
}
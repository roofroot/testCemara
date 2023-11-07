package coco.man.recorder;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import coco.man.recorder.util.LogUtils;

//Render中一般是用OpenGl做来图形图像处理，需要了解Android提供的OpenGL ES用法
public class TestRender implements GLSurfaceView.Renderer {
    private final String TAG = getClass().getSimpleName();
 
    private Triangle mTriangle;
 
    public TestRender(){
 
    }
 
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            LogUtils.INSTANCE.d("surfaceCreated()");
            //surface被创建后需要做的处理
            //这里是设置背景颜色
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            // 初始化一个三角形
            mTriangle = new Triangle();
            // 初始化一个正方形
        }catch (Exception e){

        }
    }
 
    // 渲染窗口大小发生改变或者屏幕方法发生变化时候回调
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        try {
            LogUtils.INSTANCE.d("surfaceChanged()");

            // 设置OpenGL视口的位置与大小, 最终图像会被输出到视口上显示
            //      在此处对应的是在GlSurfaceView的Surface上现实在哪个区域
            // 该API定义时的坐标方向与OpenGl二维图形中坐标方向相同，
            //      以(0,0)为的左下角，向右为x轴正方向，向上为y轴正方向.
            //      最终输出的画面会等比例缩放到在这个区域上，但设置的区域超出实际Surface的部分不会显示。
            GLES20.glViewport(0, 0, width, height);
        }catch (Exception e){

        }
    }
 
    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            LogUtils.INSTANCE.d( "onDrawFrame()");

//        mTriangle.draw();
        }catch (Exception e){

        }

    }
 
}

package coco.man.recorder.work

import android.animation.ObjectAnimator

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera.ACTION_NEW_PICTURE
import android.media.*
import android.net.Uri
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.OrientationEventListener
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_BLOCK_PRODUCER
import androidx.camera.core.impl.CameraDeviceSurfaceManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.drawToBitmap
import androidx.lifecycle.LifecycleOwner
import coco.man.recorder.MyRender
import coco.man.recorder.TestRender
import coco.man.recorder.base.SimpleBaseWork
import coco.man.recorder.databinding.LayoutVideoBinding
import coco.man.recorder.util.BegPermissionsUtils
import coco.man.recorder.util.LogUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.microedition.khronos.opengles.GL10
import kotlin.concurrent.timerTask


/**
 * coco man
 * 2023/3/12
 **/
class VideoWork(
    binding: LayoutVideoBinding,
    context: Activity,
    config: CemaraConfig
) :
    SimpleBaseWork<LayoutVideoBinding>(binding, context) {
    val mConfig = config
    val imageCapture = ImageCapture.Builder()
        .setTargetResolution(config.getResolution())
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
    val cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(config.getFacing()).build()


    val imageAnalysis =
        ImageAnalysis.Builder().setBackgroundExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setImageQueueDepth(STRATEGY_BLOCK_PRODUCER)
            .build()
    var onTakePhotoFinish: ((path: String) -> Unit)? = null
    var onTakeBitFinish: ((path: Bitmap) -> Unit)? = null

    lateinit var begPermissionsUtils: BegPermissionsUtils
    val recorder = Recorder.Builder()
        .setAspectRatio(AspectRatio.RATIO_4_3)
        .setExecutor(ContextCompat.getMainExecutor(context))
        .setTargetVideoEncodingBitRate(1000000)
        .setQualitySelector(QualitySelector.from(Quality.HD))
        .build()
    val videoCapture = VideoCapture.withOutput(recorder)


    init {


        begPermissionsUtils =
            BegPermissionsUtils(context, object : BegPermissionsUtils.TodoBackFromBeg {
                override fun backTodo(requestCode: Int) {
                    init(context)
                }

                override fun cancelTodo(requestCode: Int) {
                    getActivity()?.finish()
                }

                override fun settingBack(requsetCode: Int) {
                    begPermissionsUtils.checkPermissions(requsetCode)
                }
            })
        if (begPermissionsUtils.checkPermissions(BegPermissionsUtils.CAMERA_CODE)) {
            init(context)
        }


    }


    fun init(context: Activity) {
        val orientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture.targetRotation = rotation
                imageAnalysis.targetRotation = rotation
                videoCapture.targetRotation = rotation
            }
        }
        orientationEventListener.enable()
        binding.preview.setEGLContextClientVersion(2)


        binding.preview.setRenderer(MyRender({
            binding.preview.requestRender()
        }, {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                var preview: Preview =
                    Preview.Builder().setTargetResolution(mConfig.getResolution()).build()




                preview.setSurfaceProvider(object : Preview.SurfaceProvider {
                    override fun onSurfaceRequested(request: SurfaceRequest) {

                        val surface = Surface(it)
                        request.provideSurface(
                            surface,
                            ContextCompat.getMainExecutor(context),
                            {

                            })
                        recorder.onSurfaceRequested(request)


                    }

                })



                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner,
                    cameraSelector,
                    imageAnalysis,
//                    videoCapture,
                    imageCapture,
                    preview
                )


            }, ContextCompat.getMainExecutor(context))
            binding.preview.renderMode = RENDERMODE_WHEN_DIRTY
            binding.preview.requestRender()

        }))





        imageAnalysis.setAnalyzer(imageAnalysis.backgroundExecutor!!, {

            LogUtils.e("dfafasdfasddfs")

            val rect = it.cropRect
            val bitmap = it.toBitmap()
            val canvas = Canvas(bitmap)
            val rotationDegrees = it.imageInfo.rotationDegrees
            val paint = Paint()
            paint.setColor(Color.BLUE)


            LogUtils.e("${rotationDegrees}")

            val rect2 =
                Rect(it.cropRect.left, it.cropRect.top, it.cropRect.bottom, it.cropRect.right)
            LogUtils.e("rect2:" + rect2.toString())
            LogUtils.e("rect:" + rect.toString())


//            val matrix = Matrix()
//            matrix.postRotate(rotationDegrees.toFloat())
//            matrix.postTranslate(0f, rect2.bottom.toFloat())
//
//            matrix.postScale(
//                rect.width().toFloat() / it.cropRect.height().toFloat(),
//                rect.height().toFloat() / it.cropRect.width().toFloat()
//            )
//            matrix.postScale(-1f, 1f);
//            matrix.postTranslate(rect.width().toFloat(), 0f);
//            canvas.drawBitmap(bitmap, matrix, paint)

            canvas.drawRect(
                rect.left.toFloat(),
                rect.top.toFloat(),
                rect.right.toFloat(),
                rect.bottom.toFloat() / 2,
                paint
            )
//            binding.preview2.holder.surface.unlockCanvasAndPost(canvas)
            canvas.save()
            if (takepic) {
                takepic = false
                onTakeBitFinish?.let { me ->
                    me(bitmap)
                }
            }
            it.close()

        })


    }

    private var takepic = false

    fun doTakePicture() {

        takepic = true

        getActivity()?.let { context ->
            imageCapture?.let { imageCapture ->
                val photoFile = File(getSaveFilePath(context))
                val metadata = ImageCapture.Metadata().apply {
                    isReversedHorizontal =
                        cameraSelector.lensFacing == CameraSelector.LENS_FACING_FRONT
                }
                // 指定保存图片路径，并设置metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata)
                    .build()
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            LogUtils.e("Photo capture error: ${exc.message}")
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)

                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                context.sendBroadcast(
                                    Intent(ACTION_NEW_PICTURE, savedUri)
                                )
                            }

                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(savedUri.toFile().extension)
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(savedUri.toFile().absolutePath),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                context.runOnUiThread {
                                    onTakePhotoFinish?.let {
                                        it(photoFile.absolutePath)
                                    }
                                }
                            }
                        }
                    })
            }
        }

    }

    fun getSaveFilePath(context: Context): String {
        val file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return if (file != null) {
            file.absolutePath +
                    File.separator + System.currentTimeMillis() + ".jpg"
        } else ""
    }

    fun getSaveVideoFilePath(context: Context): String {
        val file = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return if (file != null) {
            file.absolutePath +
                    File.separator + System.currentTimeMillis() + ".mp4"
        } else ""
    }

    var recording: Recording? = null
    fun toRecord() {
        // Create MediaStoreOutputOptions for our recorder
        val name = getSaveVideoFilePath(getActivity()!!)
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            getActivity()!!.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

// 2. Configure Recorder and Start recording to the mediaStoreOutput.

        recording = videoCapture.output
            .prepareRecording(getActivity()!!, mediaStoreOutput)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(getActivity()!!), {


            })

    }

    fun toStopRecord() {
        recording?.stop()
    }

    override fun onClick(v: View?) {

    }

}


package coco.man.recorder.work

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Camera.ACTION_NEW_PICTURE
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import coco.man.recorder.base.SimpleBaseWork
import coco.man.recorder.databinding.LayoutCameraBinding
import coco.man.recorder.util.BegPermissionsUtils
import coco.man.recorder.util.LogUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * coco man
 * 2023/3/12
 **/
class CameraWork(
    binding: LayoutCameraBinding,
    context: Activity,
    config: CemaraConfig
) :
    SimpleBaseWork<LayoutCameraBinding>(binding, context) {
    val mConfig = config
    val imageCapture = ImageCapture.Builder()
        .setTargetResolution(config.getResolution())
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
    val cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(config.getFacing()).build()
    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_DEFAULT)
        .setTargetRotation(config.getRotation())
        .build()
    var onTakePhotoFinish: ((path: String) -> Unit)? = null

    lateinit var begPermissionsUtils: BegPermissionsUtils
    var mediaCodec: MediaCodec? = null
    var mediaMuxer: MediaMuxer? = null

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

    fun initRecoder() {
        val mediaFormat = MediaFormat()
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 500_000)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
        mediaCodec = MediaCodec.createByCodecName(MediaFormat.MIMETYPE_VIDEO_AVC)

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
            }
        }
        orientationEventListener.enable()


        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //在这里绑定Preview
            var preview: Preview =
                Preview.Builder().setTargetResolution(mConfig.getResolution()).build()
            preview.setSurfaceProvider(binding.preview.surfaceProvider)

            cameraProvider.bindToLifecycle(
                context as LifecycleOwner, cameraSelector, imageCapture, imageAnalysis,
                preview
            )


        }, ContextCompat.getMainExecutor(context))

    }

    fun doTakePicture() {

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

    override fun onClick(v: View?) {

    }

}


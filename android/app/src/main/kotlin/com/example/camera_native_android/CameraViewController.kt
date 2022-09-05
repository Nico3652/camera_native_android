import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture


class CameraViewController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    camera: PreviewView,
) {

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null
    private val cameraPreview: PreviewView = camera
    private var cameraDevice: Camera? = null
    private var isInitialized = false
    private var currentLensFacing: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


    /**
     * To start camera preview and capture
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission", "RestrictedApi")
    fun startCamera(onInitialize: (result: Boolean?) -> Unit) {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context.applicationContext)
        try {

            //Init imageCapture
            imageCapture = ImageCapture.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
                setCameraSelector(currentLensFacing)
            }.build()

            videoCapture = VideoCapture.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
                setVideoFrameRate(30)
                setCameraSelector(currentLensFacing)
            }.build()

            cameraProviderFuture.addListener({
                try {
                    // Used to bind the lifecycle of cameras to the lifecycle owner
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    val preview: Preview = Preview.Builder().apply {
                        setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        setCameraSelector(currentLensFacing)
                    }.build()
                    preview.setSurfaceProvider(cameraPreview.surfaceProvider)
                    cameraPreview.scaleType = PreviewView.ScaleType.FIT_CENTER
                    try {
                        // Unbind use cases before rebinding
                        cameraProvider.unbindAll()

                        // Bind use cases to camera
                        cameraDevice = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            currentLensFacing,
                            preview,
                            imageCapture,
                            videoCapture
                        )

                        isInitialized = true
                        onInitialize.invoke(true)
                    } catch (exc: Exception) {
                        onInitialize.invoke(false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * To stop camera preview and capture
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopCamera(onStop: (result: Boolean?) -> Unit) {
        // if (isInitialized) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context.applicationContext)
        try {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            cameraDevice = null
            isInitialized = false
            onStop.invoke(true)
        } catch (e: Exception) {
            e.printStackTrace()
            onStop.invoke(false)
        }
        //   }
    }


}
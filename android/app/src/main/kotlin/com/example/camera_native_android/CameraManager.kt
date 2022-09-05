
import CameraViewController
import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class CameraManager {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun handle(call: MethodCall, result: MethodChannel.Result, controller: CameraViewController) {
        when {
            call.method.equals("startSession") -> {
                controller.startCamera { initialized ->
                    result.success(initialized)
                }
            }
            call.method.equals("stopSession") -> {
                controller.stopCamera { stopped ->
                    result.success(stopped)
                }
            }
        }
    }
}
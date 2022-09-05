package com.example.camera_native_android

import CameraManager
import CameraViewController
import CameraViewFactory
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // INIT THE LAYOUT VIEW
        val view: View = LayoutInflater.from(this).inflate(R.layout.camera_view, null)

        flutterEngine
            .platformViewsController
            .registry
            .registerViewFactory("<camera_view>", CameraViewFactory(view))


        // HANDLE THE CAMERA FUNCTIONS
        val cameraChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "CameraController")
        val cameraPreview: PreviewView = view.findViewById(R.id.camera_view)
        val cameraController = CameraViewController(this, this, cameraPreview)
        val cameraManager = CameraManager()

        cameraChannel.setMethodCallHandler { call: MethodCall, result: MethodChannel.Result ->
            cameraManager.handle(call,result, cameraController)
        }

    }
}

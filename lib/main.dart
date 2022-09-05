import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: CameraPage(),
    );
  }
}

class CameraPage extends StatefulWidget {
  const CameraPage({Key? key}) : super(key: key);

  @override
  State<CameraPage> createState() => _CameraPageState();
}

class _CameraPageState extends State<CameraPage> {
  MethodChannel cameraChannel = const MethodChannel("CameraController");

  Future<void> startCamera() async {
    var status = await Permission.camera.status;
    if (status.isGranted) {
      try {
        bool success = await cameraChannel.invokeMethod("startSession");
        if (success && mounted) {
          setState(() {});
        }
      } catch (e) {}
    } else if (status.isDenied) {
      var status = await Permission.camera.request();
      if (status.isGranted) {
        startCamera();
      }
    }
  }

  Future<void> stopCamera() async {
    try {
      bool success = await cameraChannel.invokeMethod("stopSession");
      if (success && mounted) {
        setState(() {});
      }
    } catch (e) {}
  }

  @override
  void initState() {
    startCamera();
    super.initState();
  }

  @override
  void dispose() {
    stopCamera();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // This is used in the platform side to register the view.
    const String viewType = '<camera_view>';
    // Pass parameters to the platform side.
    final Map<String, dynamic> creationParams = <String, dynamic>{};
    return Scaffold(
      backgroundColor: Colors.black,
      body: Stack(
        children: [
          // THE CAMERA NATIVE VIEW
          AndroidView(
            viewType: viewType,
            layoutDirection: TextDirection.ltr,
            creationParams: creationParams,
            creationParamsCodec: const StandardMessageCodec(),
          ),
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                children: [
                  Row(
                    children: const [
                      Icon(
                        Icons.clear,
                        color: Colors.white,
                        size: 30,
                      ),
                      Spacer(),
                      Icon(
                        Icons.flip_camera_android,
                        color: Colors.white,
                        size: 30,
                      ),
                    ],
                  ),
                  const SizedBox(height: 20),
                  const Align(
                    alignment: Alignment.topRight,
                    child: Icon(
                      Icons.flash_off,
                      color: Colors.white,
                      size: 30,
                    ),
                  ),
                  const SizedBox(height: 20),
                  const Align(
                    alignment: Alignment.topRight,
                    child: Icon(
                      Icons.timer,
                      color: Colors.white,
                      size: 30,
                    ),
                  ),
                  const Spacer(),
                  const Center(
                    child: Icon(
                      Icons.circle_outlined,
                      size: 80,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 20),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}

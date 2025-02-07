package com.example.medicalapp.ui.theme.screens


import android.annotation.SuppressLint
import android.content.Context
import android.util.Size
import android.view.TextureView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun LiveClassificationScreen(navController: NavHostController) {
  val context = LocalContext.current
  val lifecycleOwner = LocalContext.current as LifecycleOwner
  var labelText by remember { mutableStateOf("Initializing...") }
  val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
  
  Column(modifier = Modifier.fillMaxSize()) {
    AndroidView(
      modifier = Modifier.weight(1f),
      factory = { ctx ->
        TextureView(ctx).apply {
          post { startCamera(ctx, lifecycleOwner, this, cameraExecutor) { label ->
            labelText = label
          } }
        }
      }
    )
    Text(text = labelText, modifier = Modifier.padding(16.dp))
  }
}

@SuppressLint("UnsafeOptInUsageError")
private fun startCamera(
  context: Context,
  lifecycleOwner: LifecycleOwner,
  textureView: TextureView,
  cameraExecutor: ExecutorService,
  onLabelDetected: (String) -> Unit
) {
  val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
  cameraProviderFuture.addListener({
    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder()
      .setTargetResolution(Size(1280, 720))
      .build()
      .also { it.setSurfaceProvider {  textureView.surfaceTexture} }
    
    val imageAnalyzer = ImageAnalysis.Builder()
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
      .build()
      .also { analysis ->
        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
          processImage(imageProxy, onLabelDetected)
        }
      }
    
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
  }, ContextCompat.getMainExecutor(context))
}

@ExperimentalGetImage
private fun processImage(imageProxy: ImageProxy, onLabelDetected: (String) -> Unit) {
  val mediaImage = imageProxy.image ?: return
  val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
  val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
  
  labeler.process(image)
    .addOnSuccessListener { labels ->
      val labelText = labels.joinToString { "${it.text}: ${it.confidence}" }
      onLabelDetected(labelText)
    }
    .addOnFailureListener {
      onLabelDetected("Error: ${it.localizedMessage}")
    }
    .addOnCompleteListener {
      imageProxy.close()
    }
}

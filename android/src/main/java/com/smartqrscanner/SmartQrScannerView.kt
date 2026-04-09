package com.smartqrscanner

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class SmartQrScannerView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val previewView: PreviewView
  private val overlayView: QrOverlayView
  private val executor = Executors.newSingleThreadExecutor()

  init {
    previewView = PreviewView(context)
    addView(previewView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    
    overlayView = QrOverlayView(context)
    addView(overlayView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    startCamera()
  }

  private fun startCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(executor, QrAnalyzer())
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            val lifecycleOwner = (context as? ReactContext)?.currentActivity as? LifecycleOwner
            if (lifecycleOwner != null) {
               cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
            }
        } catch(exc: Exception) {
            exc.printStackTrace()
        }
    }, ContextCompat.getMainExecutor(context))
  }

  @SuppressLint("UnsafeOptInUsageError")
  inner class QrAnalyzer : ImageAnalysis.Analyzer {
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    private val scanner = BarcodeScanning.getClient(options)
    private var lastScanned = 0L

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes[0]
                        val boundingBox = barcode.boundingBox
                        if (boundingBox != null) {
                            overlayView.updateTarget(boundingBox, imageProxy.imageInfo.rotationDegrees, image.width, image.height)
                        }
                        
                        val rawValue = barcode.rawValue
                        val now = System.currentTimeMillis()
                        if (rawValue != null && now - lastScanned > 2000) {
                            lastScanned = now
                            fireEvent(rawValue)
                        }
                    } else {
                        overlayView.clearTarget()
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
  }

  private fun fireEvent(value: String) {
      val reactContext = context as? ReactContext
      reactContext?.getJSModule(RCTEventEmitter::class.java)?.receiveEvent(
          id,
          "topCodeScanned",
          Arguments.createMap().apply { putString("value", value) }
      )
  }
}

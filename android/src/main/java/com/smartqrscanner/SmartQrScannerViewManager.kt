package com.smartqrscanner

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.SmartQrScannerViewManagerInterface
import com.facebook.react.viewmanagers.SmartQrScannerViewManagerDelegate

@ReactModule(name = SmartQrScannerViewManager.NAME)
class SmartQrScannerViewManager : SimpleViewManager<SmartQrScannerView>(),
  SmartQrScannerViewManagerInterface<SmartQrScannerView> {
  private val mDelegate: ViewManagerDelegate<SmartQrScannerView>

  init {
    mDelegate = SmartQrScannerViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<SmartQrScannerView>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): SmartQrScannerView {
    return SmartQrScannerView(context)
  }



  companion object {
    const val NAME = "SmartQrScannerView"
  }
}

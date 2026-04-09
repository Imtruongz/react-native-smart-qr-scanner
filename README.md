# react-native-smart-qr-scanner

A high-performance, native React Native QR Scanner component with a smart, animated focus tracking UI (similar to Zalo, Momo). 

It is built directly on top of Google's **CameraX** and **MLKit Barcode Scanning** for extremely fast, offline, native-level QR detection, avoiding the heavy performance cost of bridging frame data to JavaScript.

## ✨ Features

- 🚀 **Extreme Native Performance**: Powered natively by CameraX & MLKit. No JS thread bottlenecks.
- 🎯 **Smart Zalo-style Tracking**: Automatically shrinks, scales, and animates a beautifully drawn 4-corner targeting box onto the QR code seamlessly.
- 🎩 **Hardware Accelerated**: The overlay box is drawn seamlessly via Canvas `ValueAnimator`. 
- 🔋 **Zero Config UI**: Drop-in Native component. No need to implement camera view logic, absolute fills, or CSS animations manually.
- 🏗 **Fabric & Turbo ready**: Built on React Native's New Architecture.

*Note: Currently heavily optimized and fully working for Android. iOS support is coming on the next roadmap!*

## 📦 Installation

```sh
npm install react-native-smart-qr-scanner
```

or using yarn:

```sh
yarn add react-native-smart-qr-scanner
```

### Android Configuration
Ensure your `android/app/build.gradle` has a `minSdkVersion` of at least `24` since CameraX requires modern Android APIs.
You also need camera permissions included in your `AndroidManifest.xml` (usually added by default):
```xml
<uses-permission android:name="android.permission.CAMERA" />
```

## 💻 Usage

```tsx
import React, { useState } from 'react';
import { View, StyleSheet, Text } from 'react-native';
import { SmartQrScannerView } from 'react-native-smart-qr-scanner';

export default function App() {
  const [scannedCode, setScannedCode] = useState<string | null>(null);

  return (
    <View style={styles.container}>
      {/* Simply drop the component in your view */}
      <SmartQrScannerView
        style={StyleSheet.absoluteFill}
        onCodeScanned={(event) => {
          if (!scannedCode && event.nativeEvent.value) {
             console.log("Found QR:", event.nativeEvent.value);
             setScannedCode(event.nativeEvent.value);
          }
        }}
      />
      
      {scannedCode && (
        <View style={styles.resultBox}>
          <Text>Result: {scannedCode}</Text>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  resultBox: {
    position: 'absolute',
    bottom: 40,
    alignSelf: 'center',
    backgroundColor: 'white',
    padding: 20,
    borderRadius: 10,
  }
});
```

## 🤝 Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## 📄 License

MIT

---

Made with ❤️ for the React Native Community.

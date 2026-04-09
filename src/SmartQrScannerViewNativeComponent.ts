import {
  codegenNativeComponent,
  type ViewProps,
} from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

type CodeScannedEvent = Readonly<{
  value: string;
}>;

interface NativeProps extends ViewProps {
  onCodeScanned?: DirectEventHandler<CodeScannedEvent>;
}

export default codegenNativeComponent<NativeProps>('SmartQrScannerView');

import type { ViewProps } from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

type CodeScannedEvent = Readonly<{
  value: string;
}>;

export interface SmartQrScannerViewProps extends ViewProps {
  onCodeScanned?: DirectEventHandler<CodeScannedEvent>;
}

export function SmartQrScannerView(_props: SmartQrScannerViewProps): never {
  throw new Error(
    "'react-native-smart-qr-scanner' is only supported on native platforms."
  );
}

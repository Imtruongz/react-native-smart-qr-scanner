import type { ViewProps } from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

type CodeScannedEvent = Readonly<{
  value: string;
}>;

export interface SmartQrScannerViewProps extends ViewProps {
  onCodeScanned?: DirectEventHandler<CodeScannedEvent>;
}

import SmartQrScannerViewNativeComponent from './SmartQrScannerViewNativeComponent';

export function SmartQrScannerView(props: SmartQrScannerViewProps) {
  return <SmartQrScannerViewNativeComponent {...props} />;
}

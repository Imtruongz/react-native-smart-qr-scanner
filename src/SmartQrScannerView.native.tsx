import type { ViewProps } from 'react-native';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

type CodeScannedEvent = Readonly<{
  value: string;
}>;

export interface SmartQrScannerViewProps extends ViewProps {
  onCodeScanned?: DirectEventHandler<CodeScannedEvent>;
}

import RTNSmartQrScannerViewNativeComponent from './RTNSmartQrScannerViewNativeComponent';

export function SmartQrScannerView(props: SmartQrScannerViewProps) {
  return <RTNSmartQrScannerViewNativeComponent {...props} />;
}

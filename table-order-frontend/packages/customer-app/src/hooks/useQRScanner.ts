import { useState, useCallback, useRef } from 'react';
import type { QRCodeData } from '../types';

export function useQRScanner() {
  const [isScanning, setIsScanning] = useState(false);
  const [hasPermission, setHasPermission] = useState<boolean | null>(null);
  const [error, setError] = useState<string | null>(null);
  const scannerRef = useRef<unknown>(null);

  const startScan = useCallback(
    async (
      elementId: string,
      onSuccess: (data: QRCodeData) => void
    ) => {
      setError(null);
      try {
        const { Html5Qrcode } = await import('html5-qrcode');
        const scanner = new Html5Qrcode(elementId);
        scannerRef.current = scanner;

        await scanner.start(
          { facingMode: 'environment' },
          { fps: 10, qrbox: { width: 250, height: 250 } },
          (decodedText) => {
            try {
              const data = JSON.parse(decodedText) as QRCodeData;
              if (data.storeCode && data.totalTables) {
                scanner.stop().catch(() => {});
                setIsScanning(false);
                onSuccess(data);
              } else {
                setError('유효하지 않은 QR 코드입니다');
              }
            } catch {
              setError('유효하지 않은 QR 코드입니다');
            }
          },
          () => {
            // QR not found in frame - ignore
          }
        );
        setIsScanning(true);
        setHasPermission(true);
      } catch {
        setHasPermission(false);
        setError('카메라 접근이 거부되었습니다. 수동 입력을 이용해주세요.');
      }
    },
    []
  );

  const stopScan = useCallback(async () => {
    const scanner = scannerRef.current as { stop: () => Promise<void> } | null;
    if (scanner) {
      await scanner.stop().catch(() => {});
      scannerRef.current = null;
    }
    setIsScanning(false);
  }, []);

  return { isScanning, hasPermission, error, startScan, stopScan };
}

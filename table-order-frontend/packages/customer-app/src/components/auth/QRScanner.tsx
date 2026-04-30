import { useEffect, useRef } from 'react';
import { Camera, Keyboard } from 'lucide-react';
import { useQRScanner } from '../../hooks/useQRScanner';
import type { QRCodeData } from '../../types';

interface QRScannerProps {
  onScan: (data: QRCodeData) => void;
  onFallback: () => void;
}

export function QRScanner({ onScan, onFallback }: QRScannerProps) {
  const { isScanning, error, startScan, stopScan } = useQRScanner();
  const mountedRef = useRef(false);

  useEffect(() => {
    if (!mountedRef.current) {
      mountedRef.current = true;
      startScan('qr-reader', onScan);
    }
    return () => { stopScan(); };
  }, [startScan, stopScan, onScan]);

  return (
    <div className="flex flex-col items-center gap-4" data-testid="qr-scanner">
      <div className="flex items-center gap-2 text-gray-600">
        <Camera className="w-5 h-5" />
        <span className="text-sm font-medium">QR 코드를 스캔해주세요</span>
      </div>
      <div
        id="qr-reader"
        className="w-72 h-72 bg-gray-900 rounded-lg overflow-hidden"
      />
      {error && (
        <p className="text-red-500 text-sm" role="alert">{error}</p>
      )}
      {!isScanning && (
        <p className="text-gray-400 text-sm">카메라를 준비하는 중...</p>
      )}
      <button
        onClick={onFallback}
        className="flex items-center gap-2 px-4 py-3 text-sm text-gray-500 hover:text-gray-700 min-h-touch"
        data-testid="qr-fallback-button"
      >
        <Keyboard className="w-4 h-4" />
        수동으로 입력하기
      </button>
    </div>
  );
}

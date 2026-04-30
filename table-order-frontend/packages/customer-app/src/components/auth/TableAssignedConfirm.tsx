import { useEffect, useState } from 'react';
import { CheckCircle } from 'lucide-react';

interface TableAssignedConfirmProps {
  tableNumber: number;
  onConfirm: () => void;
}

export function TableAssignedConfirm({ tableNumber, onConfirm }: TableAssignedConfirmProps) {
  const [countdown, setCountdown] = useState(3);

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          onConfirm();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, [onConfirm]);

  return (
    <div className="flex flex-col items-center gap-4 text-center" data-testid="table-assigned-confirm">
      <CheckCircle className="w-16 h-16 text-green-500" />
      <h2 className="text-2xl font-bold text-gray-800">설정 완료!</h2>
      <p className="text-lg text-gray-600">
        <span className="font-bold text-primary-700" data-testid="table-assigned-number">
          테이블 {tableNumber}번
        </span>
        으로 설정되었습니다
      </p>
      <p className="text-sm text-gray-400">{countdown}초 후 메뉴 화면으로 이동합니다</p>
      <button
        onClick={onConfirm}
        className="px-6 py-3 bg-primary-600 text-white rounded-lg font-medium min-h-touch hover:bg-primary-700"
        data-testid="table-assigned-ok-button"
      >
        바로 이동
      </button>
    </div>
  );
}

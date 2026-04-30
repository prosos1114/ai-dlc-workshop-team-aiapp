import { useState } from 'react';
import { QRScanner } from '../components/auth/QRScanner';
import { ManualSetupForm } from '../components/auth/ManualSetupForm';
import { PasswordInput } from '../components/auth/PasswordInput';
import { TableAssignedConfirm } from '../components/auth/TableAssignedConfirm';
import { useTableAuth } from '../hooks/useTableAuth';
import type { QRCodeData } from '../types';

type SetupStep = 'scan' | 'manual' | 'password' | 'assigned';

export default function SetupPage() {
  const [step, setStep] = useState<SetupStep>('scan');
  const [qrData, setQrData] = useState<QRCodeData | null>(null);
  const [assignedTableNumber, setAssignedTableNumber] = useState<number>(0);
  const { login, isLoading, error, clearError } = useTableAuth();

  const handleQRScan = (data: QRCodeData) => {
    setQrData(data);
    setStep('password');
  };

  const handleManualSubmit = (data: QRCodeData) => {
    setQrData(data);
    setStep('password');
  };

  const handlePasswordSubmit = async (password: string) => {
    if (!qrData) return;
    clearError();
    // Login will navigate to /menu on success via useTableAuth
    // For table assignment, the server returns tableNumber in response
    await login(qrData.storeCode, password);
    // If login succeeds, useTableAuth navigates to /menu
    // We show assigned confirmation if we have a table number
    // For now, the auth flow handles navigation
  };

  const handleAssignedConfirm = () => {
    // Navigation is handled by useTableAuth on successful login
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-primary-50 to-blue-100 p-4">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
        <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
          테이블오더 설정
        </h1>

        {step === 'scan' && (
          <QRScanner
            onScan={handleQRScan}
            onFallback={() => setStep('manual')}
          />
        )}

        {step === 'manual' && (
          <div className="flex flex-col items-center gap-4">
            <ManualSetupForm onSubmit={handleManualSubmit} />
            <button
              onClick={() => setStep('scan')}
              className="text-sm text-gray-500 hover:text-gray-700 min-h-touch"
            >
              QR 스캔으로 돌아가기
            </button>
          </div>
        )}

        {step === 'password' && qrData && (
          <PasswordInput
            storeCode={qrData.storeCode}
            onSubmit={handlePasswordSubmit}
            isLoading={isLoading}
            error={error}
          />
        )}

        {step === 'assigned' && (
          <TableAssignedConfirm
            tableNumber={assignedTableNumber}
            onConfirm={handleAssignedConfirm}
          />
        )}
      </div>
    </div>
  );
}

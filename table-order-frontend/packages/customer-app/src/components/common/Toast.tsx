import { CheckCircle, XCircle, Info, X } from 'lucide-react';
import { useToastStore } from '../../store/toastStore';

const iconMap = {
  success: CheckCircle,
  error: XCircle,
  info: Info,
};

const colorMap = {
  success: 'bg-green-50 border-green-200 text-green-800',
  error: 'bg-red-50 border-red-200 text-red-800',
  info: 'bg-blue-50 border-blue-200 text-blue-800',
};

export function Toast() {
  const { toasts, removeToast } = useToastStore();

  if (toasts.length === 0) return null;

  return (
    <div className="fixed top-4 right-4 z-50 flex flex-col gap-2" data-testid="toast-container">
      {toasts.map((toast) => {
        const Icon = iconMap[toast.type];
        return (
          <div
            key={toast.id}
            className={`flex items-center gap-3 px-4 py-3 rounded-lg border shadow-lg animate-slide-in ${colorMap[toast.type]}`}
            role="alert"
            data-testid={`toast-${toast.id}`}
          >
            <Icon className="w-5 h-5 flex-shrink-0" />
            <span className="text-sm font-medium">{toast.message}</span>
            <button
              onClick={() => removeToast(toast.id)}
              className="ml-2 p-1 rounded hover:bg-black/5 min-w-touch min-h-touch flex items-center justify-center"
              aria-label="닫기"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        );
      })}
    </div>
  );
}

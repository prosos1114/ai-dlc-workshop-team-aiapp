interface ConfirmDialogProps {
  isOpen: boolean;
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export function ConfirmDialog({
  isOpen,
  title,
  message,
  confirmLabel = '확인',
  cancelLabel = '취소',
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" data-testid="confirm-dialog">
      <div className="bg-white rounded-xl p-6 mx-4 max-w-sm w-full shadow-xl">
        <h3 className="text-lg font-semibold text-gray-800 mb-2">{title}</h3>
        <p className="text-gray-600 mb-6">{message}</p>
        <div className="flex gap-3">
          <button
            onClick={onCancel}
            className="flex-1 px-4 py-3 border border-gray-300 rounded-lg text-gray-700 font-medium min-h-touch hover:bg-gray-50"
            data-testid="confirm-dialog-cancel"
          >
            {cancelLabel}
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 px-4 py-3 bg-primary-600 text-white rounded-lg font-medium min-h-touch hover:bg-primary-700"
            data-testid="confirm-dialog-confirm"
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

interface LoadingSpinnerProps {
  message?: string;
  size?: 'sm' | 'md' | 'lg';
}

const sizeMap = {
  sm: 'w-6 h-6',
  md: 'w-10 h-10',
  lg: 'w-16 h-16',
};

export function LoadingSpinner({ message, size = 'md' }: LoadingSpinnerProps) {
  return (
    <div className="flex flex-col items-center justify-center p-8" data-testid="loading-spinner">
      <div
        className={`${sizeMap[size]} border-4 border-gray-200 border-t-primary-600 rounded-full animate-spin`}
        role="status"
        aria-label="로딩 중"
      />
      {message && <p className="mt-4 text-gray-500 text-sm">{message}</p>}
    </div>
  );
}

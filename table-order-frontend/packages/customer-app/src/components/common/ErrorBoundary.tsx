import React from 'react';
import { AlertTriangle } from 'lucide-react';

interface Props {
  children: React.ReactNode;
  fallback?: React.ReactNode;
}

interface State {
  hasError: boolean;
}

export class ErrorBoundary extends React.Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError(): State {
    return { hasError: true };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('ErrorBoundary caught:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        this.props.fallback ?? (
          <div className="flex flex-col items-center justify-center h-full p-8 text-center" data-testid="error-fallback">
            <AlertTriangle className="w-16 h-16 text-red-400 mb-4" />
            <h2 className="text-xl font-semibold text-gray-700 mb-2">오류가 발생했습니다</h2>
            <p className="text-gray-500 mb-4">잠시 후 다시 시도해주세요</p>
            <button
              onClick={() => this.setState({ hasError: false })}
              className="px-6 py-3 bg-primary-600 text-white rounded-lg min-h-touch min-w-touch"
              data-testid="error-retry-button"
            >
              다시 시도
            </button>
          </div>
        )
      );
    }
    return this.props.children;
  }
}

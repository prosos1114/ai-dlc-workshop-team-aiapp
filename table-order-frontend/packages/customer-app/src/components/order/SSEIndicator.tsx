import type { SSEConnectionStatus } from '../../types';

interface SSEIndicatorProps {
  status: SSEConnectionStatus;
}

const statusConfig = {
  connected: { color: 'bg-green-500', text: '실시간 연결됨' },
  reconnecting: { color: 'bg-yellow-500 animate-pulse', text: '재연결 중...' },
  disconnected: { color: 'bg-red-500', text: '연결 끊김' },
};

export function SSEIndicator({ status }: SSEIndicatorProps) {
  const config = statusConfig[status];

  return (
    <div className="flex items-center gap-2 text-xs text-gray-500" data-testid="sse-indicator">
      <span className={`w-2 h-2 rounded-full ${config.color}`} />
      <span>{config.text}</span>
    </div>
  );
}

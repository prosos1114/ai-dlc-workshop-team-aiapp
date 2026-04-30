import { useEffect, useRef, useCallback } from 'react';
import { API, SSE_RECONNECT_DELAY, TOKEN_KEY } from '@table-order/shared';
import type { SSEEventType } from '@table-order/shared';

interface SSEOptions {
  storeId: number;
  onEvent: (type: SSEEventType, data: unknown) => void;
  enabled?: boolean;
}

export function useSSE({ storeId, onEvent, enabled = true }: SSEOptions) {
  const eventSourceRef = useRef<EventSource | null>(null);
  const reconnectTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const connect = useCallback(() => {
    if (!enabled || !storeId) return;

    const token = localStorage.getItem(TOKEN_KEY);
    const baseUrl = import.meta.env?.VITE_API_BASE_URL ?? '';
    const url = `${baseUrl}${API.ORDER_STREAM(storeId)}?token=${token}`;

    const eventSource = new EventSource(url);
    eventSourceRef.current = eventSource;

    eventSource.onmessage = (event) => {
      try {
        const parsed = JSON.parse(event.data);
        onEvent(parsed.type, parsed.data);
      } catch {
        // 파싱 실패 시 무시 (하트비트 등)
      }
    };

    eventSource.onerror = () => {
      eventSource.close();
      eventSourceRef.current = null;
      // 자동 재연결
      reconnectTimeoutRef.current = setTimeout(() => {
        connect();
      }, SSE_RECONNECT_DELAY);
    };
  }, [storeId, onEvent, enabled]);

  const disconnect = useCallback(() => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
      reconnectTimeoutRef.current = null;
    }
  }, []);

  useEffect(() => {
    connect();
    return () => disconnect();
  }, [connect, disconnect]);

  return { disconnect };
}

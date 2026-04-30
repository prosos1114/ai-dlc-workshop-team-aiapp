import { useEffect, useRef, useState, useCallback } from 'react';
import { SSE_RECONNECT_DELAY } from '@table-order/shared';
import type { SSEConnectionStatus, SSEEvent } from '../types';

const MAX_RETRIES = 5;
const API_BASE_URL = import.meta.env?.VITE_API_BASE_URL ?? 'http://localhost:8080';

export function useSSE(
  storeId: number | null,
  onEvent: (event: SSEEvent) => void
) {
  const [status, setStatus] = useState<SSEConnectionStatus>('disconnected');
  const eventSourceRef = useRef<EventSource | null>(null);
  const retryCountRef = useRef(0);
  const onEventRef = useRef(onEvent);
  onEventRef.current = onEvent;

  const connect = useCallback(() => {
    if (!storeId) return;

    const url = `${API_BASE_URL}/api/stores/${storeId}/orders/stream`;
    const es = new EventSource(url);

    es.onopen = () => {
      retryCountRef.current = 0;
      setStatus('connected');
    };

    es.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data) as SSEEvent;
        onEventRef.current(data);
      } catch {
        // ignore parse errors
      }
    };

    es.onerror = () => {
      es.close();
      if (retryCountRef.current < MAX_RETRIES) {
        setStatus('reconnecting');
        retryCountRef.current++;
        setTimeout(connect, SSE_RECONNECT_DELAY);
      } else {
        setStatus('disconnected');
      }
    };

    eventSourceRef.current = es;
  }, [storeId]);

  const disconnect = useCallback(() => {
    eventSourceRef.current?.close();
    eventSourceRef.current = null;
    setStatus('disconnected');
  }, []);

  useEffect(() => {
    connect();
    return disconnect;
  }, [connect, disconnect]);

  // Reconnect on tab visibility change
  useEffect(() => {
    const handleVisibility = () => {
      if (document.visibilityState === 'visible' && storeId) {
        if (!eventSourceRef.current || eventSourceRef.current.readyState === EventSource.CLOSED) {
          retryCountRef.current = 0;
          connect();
        }
      }
    };
    document.addEventListener('visibilitychange', handleVisibility);
    return () => document.removeEventListener('visibilitychange', handleVisibility);
  }, [connect, storeId]);

  return { status, disconnect };
}

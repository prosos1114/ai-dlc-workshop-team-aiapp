import { useEffect, useRef, useCallback } from 'react';

export function useCartAutoClose(
  isOpen: boolean,
  onClose: () => void,
  timeout = 3000
) {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const clearTimer = useCallback(() => {
    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  const resetTimer = useCallback(() => {
    clearTimer();
    if (isOpen) {
      timerRef.current = setTimeout(onClose, timeout);
    }
  }, [isOpen, onClose, timeout, clearTimer]);

  useEffect(() => {
    if (isOpen) {
      resetTimer();
    } else {
      clearTimer();
    }
    return clearTimer;
  }, [isOpen, resetTimer, clearTimer]);

  return { resetTimer };
}

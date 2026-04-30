import { create } from 'zustand';
import type { ToastItem } from '../types';

interface ToastState {
  toasts: ToastItem[];
}

interface ToastActions {
  showToast: (message: string, type?: ToastItem['type'], duration?: number) => void;
  removeToast: (id: number) => void;
}

export type ToastStore = ToastState & ToastActions;

export const useToastStore = create<ToastStore>()((set) => ({
  toasts: [],

  showToast: (message, type = 'info', duration = 3000) => {
    const id = Date.now();
    set((state) => ({
      toasts: [...state.toasts, { id, message, type, duration }],
    }));
    setTimeout(() => {
      set((state) => ({
        toasts: state.toasts.filter((t) => t.id !== id),
      }));
    }, duration);
  },

  removeToast: (id) =>
    set((state) => ({
      toasts: state.toasts.filter((t) => t.id !== id),
    })),
}));

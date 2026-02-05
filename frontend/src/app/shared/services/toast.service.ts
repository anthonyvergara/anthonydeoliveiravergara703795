import { Injectable, signal } from '@angular/core';
import { ToastMessage } from '../models/toast.model';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toasts = signal<ToastMessage[]>([]);

  getToasts() {
    return this.toasts;
  }

  showSuccess(message: string, duration: number = 3000): void {
    this.show({ type: 'success', message }, duration);
  }

  showError(message: string, duration: number = 4000): void {
    this.show({ type: 'error', message }, duration);
  }

  showInfo(message: string, duration: number = 3000): void {
    this.show({ type: 'info', message }, duration);
  }

  private show(toast: ToastMessage, duration: number): void {
    const toastWithId = { ...toast, id: Date.now() } as ToastMessage & { id: number };
    this.toasts.update(toasts => [...toasts, toastWithId]);

    setTimeout(() => {
      this.remove(toastWithId);
    }, duration);
  }

  remove(toast: ToastMessage): void {
    this.toasts.update(toasts => toasts.filter(t => t !== toast));
  }
}


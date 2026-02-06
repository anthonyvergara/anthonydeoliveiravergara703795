import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AlbumNotification } from '../models/notification.model';
import type { Client, StompSubscription } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private client: Client | null = null;
  private subscription: StompSubscription | null = null;

  private notificationsSubject = new BehaviorSubject<AlbumNotification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  async connect(): Promise<void> {
    if (this.client?.connected || this.client?.active) {
      console.log('WebSocket já está conectado ou conectando.');
      return;
    }

    console.log('Iniciando conexão WebSocket...');
    try {
      const { default: SockJS } = await import('sockjs-client');
      const { Client } = await import('@stomp/stompjs');

      const socket = new SockJS('http://localhost:8080/ws');

      this.client = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log('✅ WebSocket conectado com sucesso');
          this.subscribeToAlbums();
        },
        onStompError: (frame) => {
          console.warn('⚠️ STOMP error (não bloqueante):', frame);
        },
        onWebSocketError: (event) => {
          console.warn('⚠️ WebSocket error (não bloqueante):', event);
        },
      });

      this.client.activate();
    } catch (error) {
      console.error('❌ Erro crítico ao tentar conectar o WebSocket:', error);
    }
  }

  private subscribeToAlbums(): void {
    if (!this.client) return;

    this.subscription = this.client.subscribe('/topic/albums', (message) => {
      const notification: AlbumNotification = JSON.parse(message.body);
      notification.read = false;

      const currentNotifications = this.notificationsSubject.value;
      this.notificationsSubject.next([notification, ...currentNotifications]);

      this.updateUnreadCount();
    });
  }

  disconnect(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
    console.log('🔌 WebSocket desconectado.');
  }

  markAsRead(notificationId: number): void {
    const notifications = this.notificationsSubject.value.map(n =>
      n.id === notificationId ? { ...n, read: true } : n
    );
    this.notificationsSubject.next(notifications);
    this.updateUnreadCount();
  }

  markAllAsRead(): void {
    const notifications = this.notificationsSubject.value.map(n => ({ ...n, read: true }));
    this.notificationsSubject.next(notifications);
    this.updateUnreadCount();
  }

  clearAll(): void {
    this.notificationsSubject.next([]);
    this.unreadCountSubject.next(0);
  }

  private updateUnreadCount(): void {
    const unread = this.notificationsSubject.value.filter(n => !n.read).length;
    this.unreadCountSubject.next(unread);
  }
}


import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification.service';
import { AlbumNotification } from '../../models/notification.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-bell.component.html',
  styleUrl: './notification-bell.component.scss'
})
export class NotificationBellComponent implements OnInit, OnDestroy {
  notifications: AlbumNotification[] = [];
  unreadCount = 0;
  isOpen = false;

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.notificationService.notifications$.subscribe(notifications => {
      this.notifications = notifications;
    });

    this.notificationService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });
  }

  ngOnDestroy(): void {
    this.notificationService.disconnect();
  }


  toggleDropdown(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.notificationService.markAllAsRead();
    }
  }

  closeDropdown(): void {
    this.isOpen = false;
  }

  navigateToArtist(artistId: number): void {
    this.closeDropdown();
    this.router.navigate(['/artists', artistId]);
  }

  clearAll(): void {
    this.notificationService.clearAll();
  }

  getTimeAgo(timestamp: string): string {
    const now = new Date();
    const notificationTime = new Date(timestamp);
    const diffMs = now.getTime() - notificationTime.getTime();
    const diffMins = Math.floor(diffMs / 60000);

    if (diffMins < 1) return 'Agora';
    if (diffMins < 60) return `${diffMins}m atrás`;

    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours}h atrás`;

    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays}d atrás`;
  }
}


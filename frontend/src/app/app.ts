import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, ChildrenOutletContexts } from '@angular/router';
import { routeAnimations } from './shared/animations/route-animations';
import { ToastComponent } from './shared/components/toast/toast.component';
import { NotificationBellComponent } from './shared/components/notification-bell/notification-bell.component';
import { AuthFacade } from './core/auth/facade/auth.facade';
import { NotificationService } from './shared/services/notification.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ToastComponent, NotificationBellComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
  animations: [routeAnimations]
})
export class AppComponent implements OnInit {
  activeSection = signal('home');
  isAuthenticated = signal(false);
  username = signal('');

  menuItems = [
    { id: 'home', label: 'Home', icon: 'bar-chart-3', route: '/home' },
    { id: 'artists', label: 'Artistas', icon: 'clipboard-check', route: '/artists' },
  ];

  constructor(
    private router: Router,
    private contexts: ChildrenOutletContexts,
    private authFacade: AuthFacade,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.authFacade.getAuthState$().subscribe(state => {
      this.isAuthenticated.set(state.isAuthenticated);
      this.username.set(state.user?.username || '');

      if (state.isAuthenticated) {
        this.notificationService.connect();
      } else {
        this.notificationService.disconnect();
      }
    });
  }

  getRouteAnimationData() {
    return this.contexts.getContext('primary')?.route?.snapshot?.data?.['animation'];
  }

  setActiveSection(sectionId: string) {
    this.activeSection.set(sectionId);
    const menuItem = this.menuItems.find(item => item.id === sectionId);
    if (menuItem) {
      this.router.navigate([menuItem.route]);
    }
  }

  getActiveMenuLabel(): string {
    const activeItem = this.menuItems.find(item => item.id === this.activeSection());
    return activeItem ? activeItem.label : 'Home';
  }

  logout(): void {
    this.authFacade.logout();
  }

  getUserInitial(): string {
    return this.username() ? this.username().charAt(0).toUpperCase() : 'U';
  }
}


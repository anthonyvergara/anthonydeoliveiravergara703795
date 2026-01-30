import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class AppComponent {
  sidebarOpen = signal(true);
  activeSection = signal('home');

  menuItems = [
    { id: 'home', label: 'Home', icon: 'bar-chart-3', route: '/home' },
    { id: 'artists', label: 'Artistas', icon: 'clipboard-check', route: '/artists' },
  ];

  constructor(private router: Router) {}

  toggleSidebar() {
    this.sidebarOpen.set(!this.sidebarOpen());
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
}


import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Artist {
  id: number;
  name: string;
  genre: string;
  imageUrl: string;
  followers: number;
  verified: boolean;
}

@Component({
  selector: 'app-artists',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './artist-list.component.html',
  styleUrls: ['./artist-list.component.scss']
})
export class ArtistListComponent {
  searchTerm = signal('');
  currentPage = signal(1);
  itemsPerPage = 12;

  // Mock data de artistas
  mockArtists: Artist[] = [
    { id: 1, name: 'Taylor Swift', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 95000000, verified: true },
    { id: 2, name: 'The Weeknd', genre: 'R&B', imageUrl: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&h=400&fit=crop', followers: 87000000, verified: true },
    { id: 3, name: 'Billie Eilish', genre: 'Alternative', imageUrl: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=400&h=400&fit=crop', followers: 78000000, verified: true },
    { id: 4, name: 'Drake', genre: 'Hip Hop', imageUrl: 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400&h=400&fit=crop', followers: 92000000, verified: true },
    { id: 5, name: 'Ariana Grande', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&h=400&fit=crop', followers: 85000000, verified: true },
    { id: 6, name: 'Ed Sheeran', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 88000000, verified: true },
    { id: 7, name: 'Dua Lipa', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=400&h=400&fit=crop', followers: 72000000, verified: true },
    { id: 8, name: 'Post Malone', genre: 'Hip Hop', imageUrl: 'https://images.unsplash.com/photo-1511735111819-9a3f7709049c?w=400&h=400&fit=crop', followers: 65000000, verified: true },
    { id: 9, name: 'Olivia Rodrigo', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1445985543470-41fba5c3144a?w=400&h=400&fit=crop', followers: 55000000, verified: true },
    { id: 10, name: 'Bad Bunny', genre: 'Reggaeton', imageUrl: 'https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&h=400&fit=crop', followers: 71000000, verified: true },
    { id: 11, name: 'Justin Bieber', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 91000000, verified: true },
    { id: 12, name: 'SZA', genre: 'R&B', imageUrl: 'https://images.unsplash.com/photo-1458560871784-56d23406c091?w=400&h=400&fit=crop', followers: 48000000, verified: true },
    { id: 13, name: 'Travis Scott', genre: 'Hip Hop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 62000000, verified: true },
    { id: 14, name: 'Rihanna', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1494232410401-ad00d5433cfa?w=400&h=400&fit=crop', followers: 98000000, verified: true },
    { id: 15, name: 'Harry Styles', genre: 'Pop Rock', imageUrl: 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400&h=400&fit=crop', followers: 76000000, verified: true },
    { id: 16, name: 'Bruno Mars', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 68000000, verified: true },
    { id: 17, name: 'Coldplay', genre: 'Rock', imageUrl: 'https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=400&h=400&fit=crop', followers: 59000000, verified: true },
    { id: 18, name: 'Adele', genre: 'Soul', imageUrl: 'https://images.unsplash.com/photo-1415201364774-f6f0bb35f28f?w=400&h=400&fit=crop', followers: 73000000, verified: true },
    { id: 19, name: 'Kendrick Lamar', genre: 'Hip Hop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 54000000, verified: true },
    { id: 20, name: 'Lady Gaga', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=400&h=400&fit=crop', followers: 82000000, verified: true },
    { id: 21, name: 'Imagine Dragons', genre: 'Rock', imageUrl: 'https://images.unsplash.com/photo-1511735111819-9a3f7709049c?w=400&h=400&fit=crop', followers: 57000000, verified: true },
    { id: 22, name: 'Miley Cyrus', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 64000000, verified: true },
    { id: 23, name: 'Selena Gomez', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&h=400&fit=crop', followers: 79000000, verified: true },
    { id: 24, name: 'Shawn Mendes', genre: 'Pop', imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=400&fit=crop', followers: 61000000, verified: true },
  ];

  filteredArtists = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.mockArtists;

    return this.mockArtists.filter(artist =>
      artist.name.toLowerCase().includes(term) ||
      artist.genre.toLowerCase().includes(term)
    );
  });


}


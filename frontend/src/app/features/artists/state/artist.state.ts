import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Artist } from '../models/artist.model';
import { Album, AlbumImage } from '../models/album.model';

export interface ArtistStateData {
  artists: Artist[];
  total: number;
  currentPage: number;
  pageSize: number;
  searchTerm: string;
  sortBy: string;
  direction: 'ASC' | 'DESC';
  loading: boolean;
  error: string | null;
  currentArtist: Artist | null;
  albums: Album[];
  albumsTotal: number;
  albumsPage: number;
  albumsLoading: boolean;
  selectedAlbumImages: AlbumImage[];
  selectedAlbumId: number | null;
}

const initialState: ArtistStateData = {
  artists: [],
  total: 0,
  currentPage: 1,
  pageSize: 12,
  searchTerm: '',
  sortBy: 'name',
  direction: 'ASC',
  loading: false,
  error: null,
  currentArtist: null,
  albums: [],
  albumsTotal: 0,
  albumsPage: 0,
  albumsLoading: false,
  selectedAlbumImages: [],
  selectedAlbumId: null
};

@Injectable({
  providedIn: 'root'
})
export class ArtistState {
  private readonly state$ = new BehaviorSubject<ArtistStateData>(initialState);

  readonly artists$: Observable<Artist[]> = new BehaviorSubject<Artist[]>([]);
  readonly total$: Observable<number> = new BehaviorSubject<number>(0);
  readonly currentPage$: Observable<number> = new BehaviorSubject<number>(1);
  readonly pageSize$: Observable<number> = new BehaviorSubject<number>(12);
  readonly searchTerm$: Observable<string> = new BehaviorSubject<string>('');
  readonly loading$: Observable<boolean> = new BehaviorSubject<boolean>(false);
  readonly error$: Observable<string | null> = new BehaviorSubject<string | null>(null);

  // Album observables
  readonly currentArtist$: Observable<Artist | null> = new BehaviorSubject<Artist | null>(null);
  readonly albums$: Observable<Album[]> = new BehaviorSubject<Album[]>([]);
  readonly albumsTotal$: Observable<number> = new BehaviorSubject<number>(0);
  readonly albumsPage$: Observable<number> = new BehaviorSubject<number>(0);
  readonly albumsLoading$: Observable<boolean> = new BehaviorSubject<boolean>(false);
  readonly selectedAlbumImages$: Observable<AlbumImage[]> = new BehaviorSubject<AlbumImage[]>([]);
  readonly selectedAlbumId$: Observable<number | null> = new BehaviorSubject<number | null>(null);

  constructor() {
    this.state$.subscribe(state => {
      (this.artists$ as BehaviorSubject<Artist[]>).next(state.artists);
      (this.total$ as BehaviorSubject<number>).next(state.total);
      (this.currentPage$ as BehaviorSubject<number>).next(state.currentPage);
      (this.pageSize$ as BehaviorSubject<number>).next(state.pageSize);
      (this.searchTerm$ as BehaviorSubject<string>).next(state.searchTerm);
      (this.loading$ as BehaviorSubject<boolean>).next(state.loading);
      (this.error$ as BehaviorSubject<string | null>).next(state.error);

      // Album state
      (this.currentArtist$ as BehaviorSubject<Artist | null>).next(state.currentArtist);
      (this.albums$ as BehaviorSubject<Album[]>).next(state.albums);
      (this.albumsTotal$ as BehaviorSubject<number>).next(state.albumsTotal);
      (this.albumsPage$ as BehaviorSubject<number>).next(state.albumsPage);
      (this.albumsLoading$ as BehaviorSubject<boolean>).next(state.albumsLoading);
      (this.selectedAlbumImages$ as BehaviorSubject<AlbumImage[]>).next(state.selectedAlbumImages);
      (this.selectedAlbumId$ as BehaviorSubject<number | null>).next(state.selectedAlbumId);
    });
  }

  setArtists(artists: Artist[], total: number): void {
    this.updateState({ artists, total });
  }

  setCurrentPage(page: number): void {
    this.updateState({ currentPage: page });
  }

  setSearchTerm(searchTerm: string): void {
    this.updateState({ searchTerm });
  }

  setSorting(sortBy: string, direction: 'ASC' | 'DESC'): void {
    this.updateState({ sortBy, direction });
  }

  setLoading(loading: boolean): void {
    this.updateState({ loading });
  }

  setError(error: string | null): void {
    this.updateState({ error });
  }

  setCurrentArtist(artist: Artist | null): void {
    this.updateState({ currentArtist: artist });
  }

  setAlbums(albums: Album[], total: number, page: number): void {
    this.updateState({ albums, albumsTotal: total, albumsPage: page });
  }

  setAlbumsLoading(loading: boolean): void {
    this.updateState({ albumsLoading: loading });
  }

  setSelectedAlbumImages(images: AlbumImage[], albumId: number | null): void {
    this.updateState({ selectedAlbumImages: images, selectedAlbumId: albumId });
  }

  clearAlbumData(): void {
    this.updateState({
      currentArtist: null,
      albums: [],
      albumsTotal: 0,
      albumsPage: 0,
      selectedAlbumImages: [],
      selectedAlbumId: null
    });
  }

  getSnapshot(): ArtistStateData {
    return this.state$.value;
  }

  private updateState(partialState: Partial<ArtistStateData>): void {
    this.state$.next({
      ...this.state$.value,
      ...partialState
    });
  }
}


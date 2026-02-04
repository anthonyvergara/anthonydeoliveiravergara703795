import { Injectable } from '@angular/core';
import { Observable, tap, catchError, of } from 'rxjs';
import { Artist } from '../models/artist.model';
import { Album, AlbumImage } from '../models/album.model';
import { ArtistService } from '../services/artist.service';
import { ArtistState } from '../state/artist.state';

@Injectable({
  providedIn: 'root'
})
export class ArtistFacade {
  readonly artists$: Observable<Artist[]>;
  readonly total$: Observable<number>;
  readonly currentPage$: Observable<number>;
  readonly searchTerm$: Observable<string>;
  readonly loading$: Observable<boolean>;
  readonly error$: Observable<string | null>;

  // Album observables
  readonly currentArtist$: Observable<Artist | null>;
  readonly albums$: Observable<Album[]>;
  readonly albumsTotal$: Observable<number>;
  readonly albumsPage$: Observable<number>;
  readonly albumsLoading$: Observable<boolean>;
  readonly selectedAlbumImages$: Observable<AlbumImage[]>;
  readonly selectedAlbumId$: Observable<number | null>;

  constructor(
    private artistService: ArtistService,
    private artistState: ArtistState
  ) {
    this.artists$ = this.artistState.artists$;
    this.total$ = this.artistState.total$;
    this.currentPage$ = this.artistState.currentPage$;
    this.searchTerm$ = this.artistState.searchTerm$;
    this.loading$ = this.artistState.loading$;
    this.error$ = this.artistState.error$;

    // Album observables
    this.currentArtist$ = this.artistState.currentArtist$;
    this.albums$ = this.artistState.albums$;
    this.albumsTotal$ = this.artistState.albumsTotal$;
    this.albumsPage$ = this.artistState.albumsPage$;
    this.albumsLoading$ = this.artistState.albumsLoading$;
    this.selectedAlbumImages$ = this.artistState.selectedAlbumImages$;
    this.selectedAlbumId$ = this.artistState.selectedAlbumId$;
  }

  loadArtists(): void {
    const state = this.artistState.getSnapshot();
    this.artistState.setLoading(true);
    this.artistState.setError(null);

    this.artistService.getArtists(state.searchTerm, state.currentPage, state.pageSize)
      .pipe(
        tap(response => {
          this.artistState.setArtists(response.content, response.totalElements);
          this.artistState.setLoading(false);
        }),
        catchError(error => {
          this.artistState.setError('Erro ao carregar artistas');
          this.artistState.setLoading(false);
          console.error('Erro ao carregar artistas:', error);
          return of(null);
        })
      )
      .subscribe();
  }

  searchArtists(searchTerm: string): void {
    this.artistState.setSearchTerm(searchTerm);
    this.artistState.setCurrentPage(1); // Reset para página 1 ao buscar
    this.loadArtists();
  }

  goToPage(page: number): void {
    const state = this.artistState.getSnapshot();
    const totalPages = Math.ceil(state.total / state.pageSize);

    if (page >= 1 && page <= totalPages) {
      this.artistState.setCurrentPage(page);
      this.loadArtists();
    }
  }

  previousPage(): void {
    const currentPage = this.artistState.getSnapshot().currentPage;
    if (currentPage > 1) {
      this.goToPage(currentPage - 1);
    }
  }

  nextPage(): void {
    const state = this.artistState.getSnapshot();
    const totalPages = Math.ceil(state.total / state.pageSize);
    if (state.currentPage < totalPages) {
      this.goToPage(state.currentPage + 1);
    }
  }

  loadArtistDetail(artistId: number): void {
    this.artistState.setLoading(true);
    this.artistState.setError(null);

    this.artistService.getArtistById(artistId)
      .pipe(
        tap(artist => {
          this.artistState.setCurrentArtist(artist);
          this.artistState.setLoading(false);
          this.loadAlbumsByArtist(artistId);
        }),
        catchError(error => {
          this.artistState.setError('Erro ao carregar detalhes do artista');
          this.artistState.setLoading(false);
          console.error('Erro ao carregar artista:', error);
          return of(null);
        })
      )
      .subscribe();
  }

  loadAlbumsByArtist(artistId: number, page: number = 0, size: number = 10): void {
    this.artistState.setAlbumsLoading(true);

    this.artistService.getAlbumsByArtist(artistId, page, size)
      .pipe(
        tap(response => {
          this.artistState.setAlbums(response.content, response.totalElements, response.page);
          this.artistState.setAlbumsLoading(false);
        }),
        catchError(error => {
          this.artistState.setError('Erro ao carregar álbuns');
          this.artistState.setAlbumsLoading(false);
          console.error('Erro ao carregar álbuns:', error);
          return of(null);
        })
      )
      .subscribe();
  }

  loadAlbumImages(albumId: number): void {
    this.artistService.getAlbumImages(albumId)
      .pipe(
        tap(images => {
          this.artistState.setSelectedAlbumImages(images, albumId);
        }),
        catchError(error => {
          console.error('Erro ao carregar imagens do álbum:', error);
          return of([]);
        })
      )
      .subscribe();
  }

  clearSelectedAlbumImages(): void {
    this.artistState.setSelectedAlbumImages([], null);
  }

  clearArtistDetail(): void {
    this.artistState.clearAlbumData();
  }

  goToAlbumPage(artistId: number, page: number): void {
    this.loadAlbumsByArtist(artistId, page);
  }
}


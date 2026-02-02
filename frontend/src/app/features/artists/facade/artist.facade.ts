import { Injectable } from '@angular/core';
import { Observable, tap, catchError, of } from 'rxjs';
import { Artist } from '../models/artist.model';
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
}


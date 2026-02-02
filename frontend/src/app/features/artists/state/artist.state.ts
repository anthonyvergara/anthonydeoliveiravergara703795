import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Artist } from '../models/artist.model';

export interface ArtistStateData {
  artists: Artist[];
  total: number;
  currentPage: number;
  pageSize: number;
  searchTerm: string;
  loading: boolean;
  error: string | null;
}

const initialState: ArtistStateData = {
  artists: [],
  total: 0,
  currentPage: 1,
  pageSize: 12,
  searchTerm: '',
  loading: false,
  error: null
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

  constructor() {
    this.state$.subscribe(state => {
      (this.artists$ as BehaviorSubject<Artist[]>).next(state.artists);
      (this.total$ as BehaviorSubject<number>).next(state.total);
      (this.currentPage$ as BehaviorSubject<number>).next(state.currentPage);
      (this.pageSize$ as BehaviorSubject<number>).next(state.pageSize);
      (this.searchTerm$ as BehaviorSubject<string>).next(state.searchTerm);
      (this.loading$ as BehaviorSubject<boolean>).next(state.loading);
      (this.error$ as BehaviorSubject<string | null>).next(state.error);
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

  setLoading(loading: boolean): void {
    this.updateState({ loading });
  }

  setError(error: string | null): void {
    this.updateState({ error });
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


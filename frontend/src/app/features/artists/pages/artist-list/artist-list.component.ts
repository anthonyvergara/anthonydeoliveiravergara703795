import { Component, OnInit, OnDestroy, computed, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable, Subject, takeUntil } from 'rxjs';
import { ArtistFacade } from '../../facade/artist.facade';
import { Artist } from '../../models/artist.model';
import { ArtistFormComponent } from '../../components/artist-form/artist-form.component';
import { ArtistService } from '../../services/artist.service';
import { ToastService } from '../../../../shared/services/toast.service';

@Component({
  selector: 'app-artist-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ArtistFormComponent],
  templateUrl: './artist-list.component.html',
  styleUrls: ['./artist-list.component.scss']
})
export class ArtistListComponent implements OnInit, OnDestroy {
  @ViewChild(ArtistFormComponent) artistFormComponent?: ArtistFormComponent;
  private destroy$ = new Subject<void>();

  searchTerm = signal('');
  currentPage = signal(1);
  totalPages = signal(1);
  totalArtists = signal(0);
  itemsPerPage = 12;
  showArtistModal = signal(false);

  artists$!: Observable<Artist[]>;
  loading$!: Observable<boolean>;
  error$!: Observable<string | null>;

  constructor(
    private artistFacade: ArtistFacade,
    private artistService: ArtistService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.artists$ = this.artistFacade.artists$;
    this.loading$ = this.artistFacade.loading$;
    this.error$ = this.artistFacade.error$;
  }

  ngOnInit(): void {
    this.artistFacade.loadArtists();

    this.artistFacade.currentPage$
      .pipe(takeUntil(this.destroy$))
      .subscribe((page: number) => this.currentPage.set(page));

    this.artistFacade.total$
      .pipe(takeUntil(this.destroy$))
      .subscribe((total: number) => {
        this.totalArtists.set(total);
        this.totalPages.set(Math.ceil(total / this.itemsPerPage));
      });

    this.artistFacade.searchTerm$
      .pipe(takeUntil(this.destroy$))
      .subscribe((term: string) => this.searchTerm.set(term));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: (number | string)[] = [];

    if (total <= 7) {
      for (let i = 1; i <= total; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);

      if (current > 3) {
        pages.push('...');
      }

      const start = Math.max(2, current - 1);
      const end = Math.min(total - 1, current + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (current < total - 2) {
        pages.push('...');
      }

      pages.push(total);
    }

    return pages;
  });

  onSearchChange(): void {
    this.artistFacade.searchArtists(this.searchTerm());
  }

  goToPage(page: number | string): void {
    if (typeof page === 'number') {
      this.artistFacade.goToPage(page);
    }
  }

  nextPage(): void {
    this.artistFacade.nextPage();
  }

  previousPage(): void {
    this.artistFacade.previousPage();
  }

  getStartIndex(): number {
    return (this.currentPage() - 1) * this.itemsPerPage + 1;
  }

  getEndIndex(): number {
    return Math.min(this.currentPage() * this.itemsPerPage, this.totalArtists());
  }

  viewArtistDetail(artistId: number): void {
    this.router.navigate(['/artists', artistId]);
  }

  openArtistModal(): void {
    this.showArtistModal.set(true);
  }

  closeArtistModal(): void {
    this.showArtistModal.set(false);
    if (this.artistFormComponent) {
      this.artistFormComponent.reset();
    }
  }

  onCreateArtist(name: string): void {
    this.artistService.createArtist(name)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (artist) => {
          this.toastService.showSuccess(`Artista "${artist.name}" criado com sucesso!`);
          this.closeArtistModal();
          this.artistFacade.loadArtists();
        },
        error: (error) => {
          if (this.artistFormComponent) {
            const errorMessage = error.error?.detail || 'Erro ao criar artista. Tente novamente.';
            this.artistFormComponent.setError(errorMessage);
          }
        }
      });
  }
}


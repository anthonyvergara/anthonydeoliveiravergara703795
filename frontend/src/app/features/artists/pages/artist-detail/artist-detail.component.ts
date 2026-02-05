import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ArtistFacade } from '../../facade/artist.facade';
import { Artist } from '../../models/artist.model';
import { Album, AlbumImage } from '../../models/album.model';
import { AlbumFormComponent } from '../../components/album-form/album-form.component';
import { ArtistFormComponent } from '../../components/artist-form/artist-form.component';
import { UploadImagesComponent } from '../../components/upload-images/upload-images.component';
import { ArtistService } from '../../services/artist.service';
import { ToastService } from '../../../../shared/services/toast.service';

@Component({
  selector: 'app-artist-detail',
  standalone: true,
  imports: [CommonModule, AlbumFormComponent, ArtistFormComponent, UploadImagesComponent],
  templateUrl: './artist-detail.component.html',
  styleUrls: ['./artist-detail.component.scss']
})
export class ArtistDetailComponent implements OnInit, OnDestroy {
  @ViewChild(AlbumFormComponent) albumFormComponent?: AlbumFormComponent;
  @ViewChild(ArtistFormComponent) artistFormComponent?: ArtistFormComponent;
  @ViewChild(UploadImagesComponent) uploadImagesComponent?: UploadImagesComponent;
  private destroy$ = new Subject<void>();

  artist: Artist | null = null;
  albums: Album[] = [];
  albumsTotal = 0;
  albumsPage = 0;
  loading = false;
  albumsLoading = false;
  selectedAlbumImages: AlbumImage[] = [];
  selectedAlbumId: number | null = null;
  showImageModal = false;
  showAlbumModal = false;
  showArtistEditModal = false;
  showUploadModal = false;
  selectedAlbum: Album | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private artistFacade: ArtistFacade,
    private artistService: ArtistService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const artistId = this.route.snapshot.paramMap.get('id');

    if (artistId) {
      this.loadArtistData(+artistId);
      this.subscribeToState();
    } else {
      this.router.navigate(['/artists']);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.artistFacade.clearArtistDetail();
  }

  private loadArtistData(artistId: number): void {
    this.artistFacade.loadArtistDetail(artistId);
  }

  private subscribeToState(): void {
    this.artistFacade.currentArtist$
      .pipe(takeUntil(this.destroy$))
      .subscribe(artist => {
        this.artist = artist;
      });

    this.artistFacade.albums$
      .pipe(takeUntil(this.destroy$))
      .subscribe(albums => {
        this.albums = albums;
      });

    this.artistFacade.albumsTotal$
      .pipe(takeUntil(this.destroy$))
      .subscribe(total => {
        this.albumsTotal = total;
      });

    this.artistFacade.albumsPage$
      .pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        this.albumsPage = page;
      });

    this.artistFacade.loading$
      .pipe(takeUntil(this.destroy$))
      .subscribe(loading => {
        this.loading = loading;
      });

    this.artistFacade.albumsLoading$
      .pipe(takeUntil(this.destroy$))
      .subscribe(loading => {
        this.albumsLoading = loading;
      });

    this.artistFacade.selectedAlbumImages$
      .pipe(takeUntil(this.destroy$))
      .subscribe(images => {
        this.selectedAlbumImages = images;
      });

    this.artistFacade.selectedAlbumId$
      .pipe(takeUntil(this.destroy$))
      .subscribe(id => {
        this.selectedAlbumId = id;
      });
  }

  goBack(): void {
    this.router.navigate(['/artists']);
  }

  getDefaultImage(album: Album): string | null {
    const defaultImage = album.images.find(img => img.isDefault);
    return defaultImage?.fileUrl || null;
  }

  hasImages(album: Album): boolean {
    return album.images && album.images.length > 0;
  }

  viewAllImages(album: Album): void {
    if (this.hasImages(album)) {
      this.artistFacade.loadAlbumImages(album.id);
      this.showImageModal = true;
    }
  }

  closeImageModal(): void {
    this.showImageModal = false;
    this.artistFacade.clearSelectedAlbumImages();
  }

  goToAlbumPage(page: number): void {
    if (this.artist) {
      this.artistFacade.goToAlbumPage(this.artist.id, page);
    }
  }

  get totalAlbumPages(): number {
    return Math.ceil(this.albumsTotal / 10);
  }

  get currentAlbumPage(): number {
    return this.albumsPage + 1;
  }

  openAlbumModal(): void {
    this.selectedAlbum = null;
    this.showAlbumModal = true;
  }

  closeAlbumModal(): void {
    this.showAlbumModal = false;
    this.selectedAlbum = null;
    if (this.albumFormComponent) {
      this.albumFormComponent.reset();
    }
  }

  openEditArtistModal(): void {
    this.showArtistEditModal = true;
  }

  closeEditArtistModal(): void {
    this.showArtistEditModal = false;
    if (this.artistFormComponent) {
      this.artistFormComponent.reset();
    }
  }

  openEditAlbumModal(album: Album): void {
    this.selectedAlbum = album;
    this.showAlbumModal = true;
  }

  openUploadModal(album: Album): void {
    this.selectedAlbum = album;
    this.showUploadModal = true;
  }

  closeUploadModal(): void {
    this.showUploadModal = false;
    this.selectedAlbum = null;
    if (this.uploadImagesComponent) {
      this.uploadImagesComponent.reset();
    }
  }

  onSubmitArtist(data: { id?: number; name: string }): void {
    if (data.id) {
      this.artistService.updateArtist(data.id, data.name)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (artist) => {
            this.toastService.showSuccess(`Artista "${artist.name}" atualizado com sucesso!`);
            this.closeEditArtistModal();
            if (this.artist) {
              this.artistFacade.loadArtistDetail(this.artist.id);
            }
          },
          error: (error) => {
            if (this.artistFormComponent) {
              const errorMessage = error.error?.detail || 'Erro ao atualizar artista. Tente novamente.';
              this.artistFormComponent.setError(errorMessage);
            }
          }
        });
    }
  }

  onCreateAlbum(data: { id?: number; title: string; artistId: number }): void {
    if (data.id) {
      // Edição
      this.artistService.updateAlbum(data.id, data.title, data.artistId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.toastService.showSuccess(`Álbum "${data.title}" atualizado com sucesso!`);
            this.closeAlbumModal();
            if (this.artist) {
              this.artistFacade.loadArtistDetail(this.artist.id);
            }
          },
          error: (error) => {
            if (this.albumFormComponent) {
              const errorMessage = error.error?.detail || 'Erro ao atualizar álbum. Tente novamente.';
              this.albumFormComponent.setError(errorMessage);
            }
          }
        });
    } else {
      // Criação
      this.artistService.createAlbum(data.title, data.artistId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.toastService.showSuccess(`Álbum "${data.title}" criado com sucesso!`);
            this.closeAlbumModal();
            if (this.artist) {
              this.artistFacade.loadArtistDetail(this.artist.id);
            }
          },
          error: (error) => {
            if (this.albumFormComponent) {
              const errorMessage = error.error?.detail || 'Erro ao criar álbum. Tente novamente.';
              this.albumFormComponent.setError(errorMessage);
            }
          }
        });
    }
  }

  onUploadImages(data: { files: File[]; setAsDefault: boolean }): void {
    if (!this.selectedAlbum) return;

    this.artistService.uploadAlbumImages(this.selectedAlbum.id, data.files, data.setAsDefault)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          const count = data.files.length;
          this.toastService.showSuccess(`${count} ${count === 1 ? 'imagem enviada' : 'imagens enviadas'} com sucesso!`);
          this.closeUploadModal();
          if (this.artist) {
            this.artistFacade.loadArtistDetail(this.artist.id);
          }
        },
        error: (error) => {
          if (this.uploadImagesComponent) {
            const errorMessage = error.error?.detail || error.error?.message || 'Erro ao enviar imagens. Tente novamente.';
            this.uploadImagesComponent.setError(errorMessage);
          }
        }
      });
  }
}


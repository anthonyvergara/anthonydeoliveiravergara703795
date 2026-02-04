import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ArtistFacade } from '../../facade/artist.facade';
import { Artist } from '../../models/artist.model';
import { Album, AlbumImage } from '../../models/album.model';

@Component({
  selector: 'app-artist-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './artist-detail.component.html',
  styleUrls: ['./artist-detail.component.scss']
})
export class ArtistDetailComponent implements OnInit, OnDestroy {
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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private artistFacade: ArtistFacade
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
}


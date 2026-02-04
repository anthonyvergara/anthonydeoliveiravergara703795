import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ArtistResponseDto } from '../dtos/artist-response.dto';
import { AlbumResponseDto } from '../dtos/album-response.dto';
import { AlbumImage } from '../models/album.model';
import { Artist } from '../models/artist.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ArtistService {
  private readonly apiUrl = `${environment.apiUrl}${environment.endpoints.artists}`;
  private readonly albumApiUrl = `${environment.apiUrl}${environment.endpoints.albums}`;

  private readonly defaultImages = [
    'assets/artists/photo-1493225457124-a3eb161ffa5f.jpeg',
    'assets/artists/photo-1511671782779-c97d3d27a1d4.jpeg',
    'assets/artists/photo-1470229722913-7c0e2dbbafd3.jpeg',
    'assets/artists/photo-1459749411175-04bf5292ceea.jpeg',
    'assets/artists/photo-1514525253161-7a46d19cd819.jpeg',
    'assets/artists/photo-1516450360452-9312f5e86fc7.jpeg',
    'assets/artists/photo-1511735111819-9a3f7709049c.jpeg',
    'assets/artists/photo-1445985543470-41fba5c3144a.jpeg',
    'assets/artists/photo-1508700115892-45ecd05ae2ad.jpeg',
    'assets/artists/photo-1458560871784-56d23406c091.jpeg',
    'assets/artists/photo-1415201364774-f6f0bb35f28f.jpeg',
    'assets/artists/photo-1487180144351-b8472da7d491.jpeg',
    'assets/artists/photo-1498038432885-c6f3f1b912ee.jpeg',
    'assets/artists/photo-1511379938547-c1f69419868d.jpeg',
  ];

  constructor(private http: HttpClient) {}

  getArtists(searchTerm?: string, page: number = 1, pageSize: number = 12): Observable<ArtistResponseDto> {
    const pageIndex = page - 1;

    let params = new HttpParams()
      .set('albums', 'false')
      .set('page', pageIndex.toString())
      .set('size', pageSize.toString())
      .set('sortBy', 'id')
      .set('direction', 'ASC');

    return this.http.get<ArtistResponseDto>(this.apiUrl, { params }).pipe(
      map(response => {
        const content = response.content.map((artist, index) => ({
          ...artist,
          imageUrl: this.getImageForArtist(artist.id, index)
        }));

        const filteredContent = searchTerm
          ? content.filter(artist =>
              artist.name.toLowerCase().includes(searchTerm.toLowerCase())
            )
          : content;

        return {
          ...response,
          content: filteredContent,
          totalElements: searchTerm ? filteredContent.length : response.totalElements
        };
      })
    );
  }

  private getImageForArtist(artistId: number, fallbackIndex: number): string {
    const imageIndex = (artistId % this.defaultImages.length);
    return this.defaultImages[imageIndex] || this.defaultImages[fallbackIndex % this.defaultImages.length];
  }

  getArtistById(artistId: number): Observable<Artist> {
    return this.http.get<Artist>(`${this.apiUrl}/${artistId}`).pipe(
      map(artist => ({
        ...artist,
        imageUrl: this.getImageForArtist(artist.id, 0)
      }))
    );
  }

  getAlbumsByArtist(
    artistId: number,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id',
    direction: string = 'ASC'
  ): Observable<AlbumResponseDto> {
    const params = new HttpParams()
      .set('artistId', artistId.toString())
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<AlbumResponseDto>(this.albumApiUrl, { params });
  }

  getAlbumImages(albumId: number): Observable<AlbumImage[]> {
    return this.http.get<AlbumImage[]>(`${this.albumApiUrl}/${albumId}/images`);
  }
}


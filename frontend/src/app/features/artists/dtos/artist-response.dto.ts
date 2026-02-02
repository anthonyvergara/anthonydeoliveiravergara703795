import { Artist } from '../models/artist.model';

export interface ArtistResponseDto {
  content: Artist[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}


import { Album } from '../models/album.model';

export interface AlbumResponseDto {
  content: Album[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}


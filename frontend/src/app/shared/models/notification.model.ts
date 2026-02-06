export interface AlbumNotification {
  id: number;
  title: string;
  artistName: string;
  artistId: number;
  message: string;
  timestamp: string;
  type: 'ALBUM_CREATED';
  read?: boolean;
}


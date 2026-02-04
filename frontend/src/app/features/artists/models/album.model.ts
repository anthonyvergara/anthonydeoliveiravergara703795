export interface AlbumImage {
  id: number;
  fileKey: string;
  fileUrl: string | null;
  isDefault: boolean;
}

export interface Album {
  id: number;
  title: string;
  artistName: string;
  artistId: number;
  images: AlbumImage[];
}


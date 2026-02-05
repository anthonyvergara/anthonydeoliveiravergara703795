import { Component, EventEmitter, Input, Output, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-album-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './album-form.component.html',
  styleUrls: ['./album-form.component.scss']
})
export class AlbumFormComponent implements OnInit {
  @Input() albumId?: number;
  @Input() albumTitle?: string;
  @Input() artistId!: number;
  @Input() artistName: string = '';
  @Output() closeModal = new EventEmitter<void>();
  @Output() submitForm = new EventEmitter<{ id?: number; title: string; artistId: number }>();

  title = signal('');
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);
  isEditMode = signal(false);

  ngOnInit(): void {
    if (this.albumId && this.albumTitle) {
      this.isEditMode.set(true);
      this.title.set(this.albumTitle);
    }
  }

  onSubmit(): void {
    const albumTitle = this.title().trim();

    if (!albumTitle) {
      this.errorMessage.set('O título do álbum é obrigatório');
      return;
    }

    this.errorMessage.set(null);
    this.isSubmitting.set(true);
    this.submitForm.emit({
      id: this.albumId,
      title: albumTitle,
      artistId: this.artistId
    });
  }

  onClose(): void {
    this.closeModal.emit();
  }

  clearError(): void {
    this.errorMessage.set(null);
  }

  setError(message: string): void {
    this.errorMessage.set(message);
    this.isSubmitting.set(false);
  }

  reset(): void {
    this.title.set('');
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
    this.isEditMode.set(false);
  }
}


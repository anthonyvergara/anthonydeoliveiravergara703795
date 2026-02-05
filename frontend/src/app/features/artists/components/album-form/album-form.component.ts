import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-album-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './album-form.component.html',
  styleUrls: ['./album-form.component.scss']
})
export class AlbumFormComponent {
  @Input() artistId!: number;
  @Input() artistName: string = '';
  @Output() closeModal = new EventEmitter<void>();
  @Output() submitForm = new EventEmitter<{ title: string; artistId: number }>();

  albumTitle = signal('');
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  onSubmit(): void {
    const title = this.albumTitle().trim();

    if (!title) {
      this.errorMessage.set('O título do álbum é obrigatório');
      return;
    }

    this.errorMessage.set(null);
    this.isSubmitting.set(true);
    this.submitForm.emit({ title, artistId: this.artistId });
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
    this.albumTitle.set('');
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
  }
}


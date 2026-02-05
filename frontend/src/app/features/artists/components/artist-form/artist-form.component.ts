import { Component, EventEmitter, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-artist-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './artist-form.component.html',
  styleUrls: ['./artist-form.component.scss']
})
export class ArtistFormComponent {
  @Output() closeModal = new EventEmitter<void>();
  @Output() submitForm = new EventEmitter<string>();

  artistName = signal('');
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  onSubmit(): void {
    const name = this.artistName().trim();

    if (!name) {
      this.errorMessage.set('O nome do artista é obrigatório');
      return;
    }

    this.errorMessage.set(null);
    this.isSubmitting.set(true);
    this.submitForm.emit(name);
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
    this.artistName.set('');
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
  }
}


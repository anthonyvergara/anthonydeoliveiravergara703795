import { Component, EventEmitter, Input, Output, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-artist-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './artist-form.component.html',
  styleUrls: ['./artist-form.component.scss']
})
export class ArtistFormComponent implements OnInit {
  @Input() artistId?: number;
  @Input() artistName?: string;
  @Output() closeModal = new EventEmitter<void>();
  @Output() submitForm = new EventEmitter<{ id?: number; name: string }>();

  name = signal('');
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);
  isEditMode = signal(false);

  ngOnInit(): void {
    if (this.artistId && this.artistName) {
      this.isEditMode.set(true);
      this.name.set(this.artistName);
    }
  }

  onSubmit(): void {
    const artistName = this.name().trim();

    if (!artistName) {
      this.errorMessage.set('O nome do artista é obrigatório');
      return;
    }

    this.errorMessage.set(null);
    this.isSubmitting.set(true);
    this.submitForm.emit({
      id: this.artistId,
      name: artistName
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
    this.name.set('');
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
    this.isEditMode.set(false);
  }
}


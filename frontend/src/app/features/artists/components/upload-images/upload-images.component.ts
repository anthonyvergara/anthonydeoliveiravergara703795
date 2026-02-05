import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload-images',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload-images.component.html',
  styleUrls: ['./upload-images.component.scss']
})
export class UploadImagesComponent {
  @Input() albumId!: number;
  @Input() albumTitle: string = '';
  @Output() closeModal = new EventEmitter<void>();
  @Output() uploadFiles = new EventEmitter<{ files: File[]; setAsDefault: boolean }>();

  selectedFiles = signal<File[]>([]);
  previewUrls = signal<string[]>([]);
  setAsDefault = signal(false);
  isUploading = signal(false);
  errorMessage = signal<string | null>(null);
  dragOver = signal(false);

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.processFiles(Array.from(input.files));
    }
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.dragOver.set(false);

    if (event.dataTransfer?.files) {
      this.processFiles(Array.from(event.dataTransfer.files));
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.dragOver.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.dragOver.set(false);
  }

  private processFiles(files: File[]): void {
    const validFiles: File[] = [];
    const validExtensions = ['.jpg', '.jpeg', '.png'];

    files.forEach(file => {
      const extension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'));

      if (!validExtensions.includes(extension)) {
        this.errorMessage.set(`Arquivo ${file.name} não é uma imagem válida. Use apenas JPG ou PNG.`);
        return;
      }

      if (file.size > 5 * 1024 * 1024) { // 5MB
        this.errorMessage.set(`Arquivo ${file.name} é muito grande. Tamanho máximo: 5MB.`);
        return;
      }

      validFiles.push(file);
    });

    if (validFiles.length > 0) {
      this.errorMessage.set(null);
      this.selectedFiles.update(current => [...current, ...validFiles]);

      // Create previews
      validFiles.forEach(file => {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.previewUrls.update(urls => [...urls, e.target?.result as string]);
        };
        reader.readAsDataURL(file);
      });
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.update(files => files.filter((_, i) => i !== index));
    this.previewUrls.update(urls => urls.filter((_, i) => i !== index));
  }

  onSubmit(): void {
    if (this.selectedFiles().length === 0) {
      this.errorMessage.set('Selecione pelo menos uma imagem');
      return;
    }

    this.isUploading.set(true);
    this.uploadFiles.emit({
      files: this.selectedFiles(),
      setAsDefault: this.setAsDefault()
    });
  }

  onClose(): void {
    this.closeModal.emit();
  }

  setError(message: string): void {
    this.errorMessage.set(message);
    this.isUploading.set(false);
  }

  reset(): void {
    this.selectedFiles.set([]);
    this.previewUrls.set([]);
    this.setAsDefault.set(false);
    this.isUploading.set(false);
    this.errorMessage.set(null);
  }
}


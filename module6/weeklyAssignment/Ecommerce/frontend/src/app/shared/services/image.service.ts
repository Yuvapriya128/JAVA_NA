import { Injectable } from '@angular/core';

export const PLACEHOLDER_IMAGE = 'https://placehold.co/600x600/F5F7FB/6B7280?text=Product';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  onImageError(event: Event, fallbackUrl: string = PLACEHOLDER_IMAGE): void {
    const img = event.target as HTMLImageElement;
    if (img && img.src !== fallbackUrl) {
      img.src = fallbackUrl;
    }
  }

  getSafeImageUrl(imageUrl?: string, fallbackUrl: string = PLACEHOLDER_IMAGE): string {
    return imageUrl || fallbackUrl;
  }
}


// Product DTOs
export interface ProductRequestDTO {
  name: string;
  brand: string;
  category: string;
  cost: number;
  description?: string;
  stock?: number;
  minimumStock?: number;
  active?: boolean;
}

export interface ProductResponseDTO {
  id: number;
  name: string;
  brand: string;
  category: string;
  cost: number;
  description?: string;
  stock?: number;
  minimumStock?: number;
  active?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ProductUpdateDTO {
  name?: string;
  brand?: string;
  category?: string;
  cost?: number;
  description?: string;
  stock?: number;
  minimumStock?: number;
  active?: boolean;
}


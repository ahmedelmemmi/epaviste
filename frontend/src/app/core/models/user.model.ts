export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: 'BUYER' | 'SELLER' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  userId: number;
  name: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phone?: string;
  role: string;
}

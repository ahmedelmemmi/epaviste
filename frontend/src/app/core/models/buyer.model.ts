import { Notification } from './notification.model';

export interface BuyerStats {
  totalRFQsSubmitted: number;
  quotesReceived: number;
  activeOrders: number;
  completedOrders: number;
  recentNotifications: Notification[];
}

export interface Vehicle {
  id: number;
  brand: string;
  model: string;
  year: number;
  vin?: string;
  createdAt: string;
}

export interface VehicleRequest {
  brand: string;
  model: string;
  year: number;
  vin?: string;
}

export interface BuyerProfile {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: string;
  createdAt: string;
}

export interface BuyerProfileRequest {
  name: string;
  phone: string;
}

import { Notification } from './notification.model';

export interface SellerStats {
  totalRFQsReceived: number;
  activeQuotes: number;
  ordersInProgress: number;
  totalRevenue: number;
  rating: number;
  recentNotifications: Notification[];
}

export interface PaymentRecord {
  id: number;
  orderId: number;
  paymentStatus: string;
  paymentMethod: string;
  escrowReleased: boolean;
  createdAt: string;
}

export interface SellerEarnings {
  totalEarnings: number;
  totalCommission: number;
  netEarnings: number;
  pendingPayouts: number;
  paymentHistory: PaymentRecord[];
}

export interface SellerProfile {
  id: number;
  userId: number;
  name: string;
  email: string;
  phone: string;
  companyName: string;
  address: string;
  description: string;
  rating: number;
  verified: boolean;
  deliveryZones: string;
  shippingMethods: string;
  messagingEnabled: boolean;
}

export interface SellerProfileRequest {
  companyName: string;
  address: string;
  description: string;
  phone: string;
  deliveryZones: string;
  shippingMethods: string;
}

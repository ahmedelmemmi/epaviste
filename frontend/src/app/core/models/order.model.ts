export interface Order {
  id: number;
  buyerId: number;
  sellerId: number;
  quoteId: number;
  totalPrice: number;
  commissionAmount: number;
  orderStatus: string;
  createdAt: string;
}

export interface Order {
  id: number;
  buyerId: number;
  buyerName: string;
  sellerId: number;
  sellerName: string;
  quoteId: number;
  totalPrice: number;
  commissionAmount: number;
  orderStatus: string;
  createdAt: string;
}

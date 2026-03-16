export interface Quote {
  id: number;
  rfqId: number;
  sellerId: number;
  sellerName: string;
  sellerCompany?: string;
  price: number;
  condition: string;
  deliveryTime: number;
  shippingMethod: string;
  message: string;
  status: string;
  createdAt: string;
}

export interface QuoteRequest {
  rfqId: number;
  price: number;
  condition: string;
  deliveryTime: number;
  shippingMethod: string;
  message: string;
}

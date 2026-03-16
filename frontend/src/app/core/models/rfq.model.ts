export interface RFQ {
  id: number;
  buyerId: number;
  buyerName: string;
  carBrand: string;
  carModel: string;
  carYear: number;
  vin?: string;
  partName: string;
  partCategory: string;
  preferredCondition: string;
  description: string;
  location: string;
  status: string;
  createdAt: string;
  images: string[];
  quoteCount: number;
}

export interface RFQRequest {
  carBrand: string;
  carModel: string;
  carYear: number;
  vin?: string;
  partName: string;
  partCategory: string;
  preferredCondition: string;
  description: string;
  location: string;
  imageUrls?: string[];
}

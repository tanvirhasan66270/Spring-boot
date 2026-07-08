
export interface QuotationRequestModel {
  supplierId: number;
  productIds: number;
  productName: string;
  purchaseRequisitionId: number;
  validUntil: string;       // YYYY-MM-DD
  leadTimeDays: number;
  isSelected: boolean;
  receivedAt: string;        // YYYY-MM-DD
  status: 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' | 'EXPIRED' | string;
  productDescription: string;
  unitPrice: number;
  quantity: number;
  deliveryTime: string;      // YYYY-MM-DD
  warranty: string;
  notes: string;
  attachmentUrl: string;
}

export interface QuotationResponseModel {
  id: number;
  quotationNumber: string;
  validUntil: string;
  leadTimeDays: number;
  isSelected: boolean;
  receivedAt: string;
  status: 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' | 'EXPIRED';
  productDescription: string;
  unitPrice: number;
  quantity: number;
  totalPrice: number;
  deliveryTime: string;
  warranty: string;
  notes: string;
  attachmentUrl: string;
  createdAt: string;
  supplierId: number;
  supplierName: string;
  productIds: number;
  productName: string;
  purchaseRequisitionId: number;
}
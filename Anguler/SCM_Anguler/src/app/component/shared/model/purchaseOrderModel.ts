

export interface PurchaseOrderRequestModel {
  quotationId: number;
  issuedBy: number;
  totalAmount: number;
  currency: string;
  expectedDeliveryDate: string; // YYYY-MM-DD
  status: 'DRAFT' | 'ISSUED' | 'PARTIALLY_RECEIVED' | 'RECEIVED' | 'CANCELLED' | string;
}

export interface PurchaseOrderResponseModel {
  id: number;
  poNumber: string;
  quantity: number;
  totalAmount: number;
  currency: string;
  expectedDeliveryDate: string;
  status: 'DRAFT' | 'ISSUED' | 'PARTIALLY_RECEIVED' | 'RECEIVED' | 'CANCELLED' | string;
  issuedBy: number;
  createdAt: string;
  updatedAt: string;
  supplierId: number;
  supplierName: string;
  supplierEmail: string;
  purchaseRequisitionId: number;
  quotationId: number;
}

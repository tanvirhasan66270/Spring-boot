export interface OrderLineItemRequestModel {
  id: number;
  productId: number;
  quantity: number;
  unitPrice: number;
  remarks: string;
}

export interface OrderLineItemResponseModel {
  id: number;
  productId: number;
  productName: string;
  productCode: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
  itemWeightTotal: number;
  remarks: string;
}

export interface CustomerOrderRequestModel {
  customerId: number;
  deliveryAddress: string;
  estimatedDelivery: string;
  serviceType: string;
  codAmount: number;
  status: string;
  items: OrderLineItemRequestModel[];
}

export interface CustomerOrderResponseModel {
  id: number;
  orderNumber: string;
  customerId: number;
  customerName: string;
  customerEmail: string;
  itemSubtotal: number;
  weight: number;
  serviceType: string;
  codAmount: number;
  deliveryCharge: number;
  totalAmount: number;
  paidAmount: string;
  currency: string;
  status: string;
  deliveryAddress: string;
  estimatedDelivery: string;
  createdAt: string;
  lineItems: OrderLineItemResponseModel[];
}
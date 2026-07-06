export interface OrderLineItemRequestModel {
  productId: number;
  quantity: number;
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
  deliveryPhone: string;
  estimatedDelivery: string;
  serviceType: string;
  currency: string;
  codAmount: number;
  paymentMethod: string;
  status: string;
  remarks: string;
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
  currency: string;
  codAmount: number;
  deliveryCharge: number;
  totalAmount: number;
  paidAmount: string;
  dueAmount: string;
  paymentStatus: string;
  paymentMethod: string;
  status: string;
  deliveryAddress: string;
  deliveryPhone: string;
  remarks: string;
  estimatedDelivery: string;
  createdAt: string;
  lineItems: OrderLineItemResponseModel[];
}
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
  estimatedDelivery: string; // ব্যাকএন্ড অটোক্যালকুলেট করবে, তবুও ইন্টারফেসে সিঙ্ক রাখা হলো
  serviceType: string;        // STANDARD, EXPRESS, OVERNIGHT, SAME_DAY
  priority: string;           // LOW, NORMAL, HIGH, URGENT
  currency: string;
  codAmount: number;
  paymentMethod: string;      // CASH, BANK, BKASH, NAGAD, ROCKET
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
  priority: string;           
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
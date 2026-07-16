/**
 * 📥 Backend-এর PurchaseRequisitionRequestDTO-এর জন্য ফ্রন্টএন্ড মডেল
 */
export interface purchaseRequisitionRequestModel {
  requestedBy: number;
  productIds: number[];
  supplierIds: number[];
  currency: string;
  quantityRequired: number;
  urgencyLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'; // Java UrgencyLevel Enum
  requiredByDate: string; // Format: YYYY-MM-DD
  remarks: string;
}

/**
 * 📤 Backend-এর PurchaseRequisitionResponseDTO-এর জন্য ফ্রন্টএন্ড মডেল
 */
export interface purchaseRequisitionResponseModel {
  id: number;
  requestedBy: number;
  currency: string;
  quantityRequired: number;
  urgencyLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'; // Java UrgencyLevel Enum
  requiredByDate: string; // Jackson কাস্টিং ডেটা (YYYY-MM-DD)
  approvalStatus: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'; // Java PurchaseRequisitionStatus Enum
  approvedBy: number | null; // ডাটাবেজে নাল থাকতে পারে তাই টাইপ সেফটির জন্য null এলাউড
  approvedByName: string | null;
  remarks: string | null;
  createdAt: string; // ISO String format
  
  // 🎯 ফ্রন্টএন্ড UI গ্রিড/টেবিল রেন্ডারিং এর জন্য ফ্ল্যাটেন্ড অবজেক্ট কালেকশন
  productIds: number[];
  productNames: string[];
  supplierIds: number[];
  supplierNames: string[];
}
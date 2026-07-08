// ── 📊 QC EXACT RESULT ENUM SYNC ──
export type QCResultType = 'GOOD' | 'VERY_GOOD' | 'AVERAGE' | 'BAD' | string;

/**
 * 📥 QCChecklistRequestModel - চাইল্ড চেকলিস্ট ইনপুট পেলোড
 */
export interface QCChecklistRequestModel {
  inspectionId?: number; // Parent FK Link Vector
  checkpointName: string;
  isPassed: boolean;
  remarks: string;
}

/**
 * 📤 QCChecklistResponseModel - চাইল্ড চেকলিস্ট আউটবাউন্ড ডিসপ্লে ডাটা
 */
export interface QCChecklistResponseModel {
  id: number; // Database Generated PK
  checkpointName: string;
  isPassed: boolean;
  remarks: string;
  createdAt: string; // Temporal DateTime ISO ISOString
  updatedAt: string;
  inspectionId: number;
  inspectionType: string;
}

/**
 * 📥 QCInspectionRequestModel - মাস্টার কিউসি ইন্সপেকশন ইনপুট পেলোড
 */
export interface QCInspectionRequestModel {
  id?: number; // নতুন ডাটার ক্ষেত্রে অপশনাল, আপডেটের ক্ষেত্রে এটি প্রাইমারি কি হিসেবে ব্যবহৃত হবে
  grnId: number;
  productId: number;
  inspectionType: string; // e.g., 'VISUAL', 'LAB_TEST'
  inspectedBy: number;
  sampleSize: number;
  defectsFound: number;
  defectDescription: string;
  result: QCResultType; // 🎯 'GOOD' | 'VERY_GOOD' | 'AVERAGE' | 'BAD'
  certificateRef: string;
  labTestReport: string;
  inspectedAt: string; // Format Pattern: YYYY-MM-DD
  checklists: QCChecklistRequestModel[]; // 🔗 চাইল্ড কালেকশন চেইন
}

/**
 * 📤 QCInspectionResponseModel - মাস্টার কিউসি ইন্সপেকশন রেসপন্স ডিসপ্লে ডাটা
 */
export interface QCInspectionResponseModel {
  id: number;
  inspectionType: string;
  sampleSize: number;
  defectsFound: number;
  defectDescription: string;
  result: QCResultType;
  certificateRef: string;
  labTestReport: string;
  inspectedAt: string; // YYYY-MM-DD
  createdAt: string;
  updatedAt: string;
  
  // --- Flattened Relations for UI Layout ---
  grnId: number;
  grnNumber: string;
  productId: number;
  productName: string;
  inspectedBy: number;
  inspectedByName: string;
  
  checklists: QCChecklistResponseModel[]; // 🎯 ফিক্সড টাইমস্ট্যাম্প সহ চাইল্ড রেসপন্স লিস্ট
}
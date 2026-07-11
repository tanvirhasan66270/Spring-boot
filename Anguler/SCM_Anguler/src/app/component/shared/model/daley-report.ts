
export interface DailyReportRequestModel {
  warehouseId: string;
  reportDate: string;        // "YYYY-MM-DD" ফরম্যাটে এঙ্গুলার ক্যালেন্ডার বা স্ট্রিং থেকে আসবে
  totalTasksDone: number;
  issuesLogged: number;
  summary?: string | null;
  attachmentUrl?: string | null;
}


export interface DailyReportResponseModel {
  id: number;
  userId: string;            // যে ইউজার বা ম্যানেজার রিপোর্টটি তৈরি করেছে
  warehouseId: string;
  reportDate: string;        // "YYYY-MM-DD" ফরম্যাট স্ট্রিং
  totalTasksDone: number;
  issuesLogged: number;
  summary?: string | null;
  reportStatus:'DRAFT' | 'SUBMITTED' | 'APPROVED' | string;
  attachmentUrl?: string | null;
  generatedAt: string;       // ISO Date-Time String (LocalDateTime)
  updatedAt: string;       // ISO Date-Time String (LocalDateTime)

  /**
   * আলাদা DTO ছাড়া সরাসরি Map অবজেক্টের লিস্ট (যেমন: [{ "role": "ADMIN", "email": "admin@scm.com" }])
   */
  notifiedAuthorities: Array<{ [key: string]: string }>;
}

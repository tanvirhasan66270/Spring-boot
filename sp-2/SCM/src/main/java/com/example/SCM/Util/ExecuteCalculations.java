package com.example.SCM.Util;

import com.example.SCM.enumClass.ServiceType;

    public class ExecuteCalculations {

        // ইনস্ট্যান্স তৈরি করা বন্ধ করার জন্য প্রাইভেট কনস্ট্রাক্টর (Utility Class Best Practice)
        private ExecuteCalculations() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }

        /**
         * ওয়ান-টাইম ওজন, সার্ভিসের ধরন এবং সিওডি অ্যামাউন্টের ওপর ভিত্তি করে ডেলিভারি চার্জ হিসাব করার মেথড
         *
         * @param weight       পার্সেলের ওজন (KG)
         * @param serviceType  সার্ভিসের ধরন (STANDARD, EXPRESS, OVERNIGHT, SAME_DAY)
         * @param codAmount    ক্যাশ অন ডেলিভারি কালেকশন অ্যামাউন্ট
         * @return মোট ডেলিভারি চার্জ (ডেলিভারি ফি + সিওডি হ্যান্ডলিং ফি)
         */
        public static double calculateDeliveryCharge(double weight, ServiceType serviceType, double codAmount) {
            double base = 0;
            double perKg = 0;
            double charge = 0;

            // ১. সার্ভিস টাইপ অনুযায়ী বেস এবং প্রতি কেজির রেট নির্ধারণ
            switch (serviceType) {
                case STANDARD -> { base = 60; perKg = 20; }
                case EXPRESS -> { base = 100; perKg = 35; }
                case OVERNIGHT -> { base = 180; perKg = 50; }
                case SAME_DAY -> { base = 250; perKg = 60; }
            }

            // ২. ওজনের ওপর ভিত্তি করে বেস ও অতিরিক্ত কেজির চার্জ হিসাব (নির্ভুল গাণিতিক ব্র্যাকেটসহ)
            if (weight < 1) {
                charge = base;
            } else {
                charge = base + ((weight - 1) * perKg);
            }

            // ৩. সিоডি ফি (১.৫%) হিসাব করে মূল চার্জের সাথে যোগ করা
            if (codAmount > 0) {
                charge += codAmount * 0.015;
            }

            return charge;
        }

        /**
         * পণ্যের মোট দাম (Line Total) হিসাব করার মেথড
         *
         * @param quantity  পণ্যের পরিমাণ
         * @param unitPrice প্রতি পিস পণ্যের একক মূল্য
         * @return পণ্যের মোট লাইন টোটাল দাম
         */
        public static double calculateLineTotal(int quantity, double unitPrice) {
            return quantity * unitPrice;
        }
    }


package com.example.SCM.Util;

import com.example.SCM.entity.OrderLineItem;
import com.example.SCM.enumClass.ServiceType;
import java.util.List;

public class ExecuteCalculations {

    // ইনস্ট্যান্স তৈরি করা বন্ধ করার জন্য প্রাইভেট কনস্ট্রাক্টর (Utility Class Best Practice)
    private ExecuteCalculations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * ওয়ান-টাইম ওজন, সার্ভিসের ধরন এবং সিওডি অ্যামাউন্টের ওপর ভিত্তি করে ডেলিভারি চার্জ হিসাব করার মেথড
     */
    public static double calculateDeliveryCharge(double weight, ServiceType serviceType, double codAmount) {
        double base = 0;
        double perKg = 0;
        double charge = 0;

        // ১.prefix সার্ভিস টাইপ অনুযায়ী বেস এবং প্রতি কেজির রেট নির্ধারণ
        switch (serviceType) {
            case STANDARD -> { base = 60; perKg = 20; }
            case EXPRESS -> { base = 100; perKg = 35; }
            case OVERNIGHT -> { base = 180; perKg = 50; }
            case SAME_DAY -> { base = 250; perKg = 60; }
        }

        // ২. ওজনের ওপর ভিত্তি করে বেস ও অতিরিক্ত কেজির চার্জ হিসাব
        if (weight < 1) {
            charge = base;
        } else {
            charge = base + ((weight - 1) * perKg);
        }

        // ৩. সিওডি ফি (১.৫%) হিসাব করে মূল চার্জের সাথে যোগ করা
        if (codAmount > 0) {
            charge += codAmount * 0.015;
        }

        return charge;
    }

    /**
     * পণ্যের মোট দাম (Line Total) হিসাব করার মেথড
     */
    public static double calculateLineTotal(int quantity, double unitPrice) {
        return quantity * unitPrice;
    }

    /**
     * 🆕 অর্ডারের সব আইটেমগুলোর দাম যোগ করে itemSubtotal বের করার মেথড
     * * @param lineItems অর্ডারের আইটেম লিস্ট
     * @return সব প্রোডাক্টের দামের সমষ্টি
     */
    public static double calculateItemSubtotal(List<OrderLineItem> lineItems) {
        if (lineItems == null) return 0.0;
        return lineItems.stream()
                .mapToDouble(OrderLineItem::getLineTotal)
                .sum();
    }

    /**
     * 🆕 সব আইটেমের মোট ওজনের যোগফল থেকে পুরো পার্সেলের মোট ওজন বের করার মেথড
     * * @param lineItems অর্ডারের আইটেম লিস্ট
     * @return পুরো অর্ডারের সর্বমোট ওজন (KG)
     */
    public static double calculateTotalOrderWeight(List<OrderLineItem> lineItems) {
        if (lineItems == null) return 0.0;
        return lineItems.stream()
                .mapToDouble(OrderLineItem::getItemWeightTotal)
                .sum();
    }
}
package com.bank.customer_service.util;

public final class MaskingUtil {

    private MaskingUtil() {}

    public static String maskAadhaar(String aadhaar) {
        // Expecting last 4 digits only or full number
        if (aadhaar == null || aadhaar.length() < 4) return null;
        return "XXXX-XXXX-" + aadhaar.substring(aadhaar.length() - 4);
    }

    public static String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return null;
        return pan.substring(0, 2) + "XXXXX" + pan.substring(pan.length() - 1);
    }
}

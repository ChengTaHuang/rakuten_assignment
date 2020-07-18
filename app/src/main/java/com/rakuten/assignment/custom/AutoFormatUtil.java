package com.rakuten.assignment.custom;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AutoFormatUtil {

    private static final String FORMAT_NO_DECIMAL = "###,###";

    private static final String FORMAT_WITH_DECIMAL = "###,###.####";

    public static int getCharOccurance(String input, char c) {
        int occurance = 0;
        char[] chars = input.toCharArray();
        for (char thisChar : chars) {
            if (thisChar == c) {
                occurance++;
            }
        }
        return occurance;
    }

    public static String extractDigits(String input) {
        return input.replaceAll("\\D+", "");
    }

    public static String formatToStringWithoutDecimal(double value) {
        NumberFormat formatter = new DecimalFormat(FORMAT_NO_DECIMAL);
        return formatter.format(value);
    }

    public static String formatToStringWithoutDecimal(String value) {
        NumberFormat formatter = new DecimalFormat(FORMAT_NO_DECIMAL);
        return formatter.format(new BigDecimal(value));
    }

    public static String formatWithDecimal(String price) {
        return formatWithDecimal(Double.parseDouble(price));
    }

    public static String formatWithDecimal(double price) {
        NumberFormat formatter = new DecimalFormat(FORMAT_WITH_DECIMAL);
        return formatter.format(price);
    }
}
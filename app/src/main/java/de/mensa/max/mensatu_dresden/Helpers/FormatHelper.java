package de.mensa.max.mensatu_dresden.Helpers;

public class FormatHelper {

    /**
     * @param oldPriceFormat x.x
     * @return new price format x.xx
     */
    public static String formatPricing(String oldPriceFormat) {
        if (oldPriceFormat.charAt(oldPriceFormat.length() - 2) == '.') {
            return oldPriceFormat + '0';
        } else {
            return oldPriceFormat;
        }
    }
}

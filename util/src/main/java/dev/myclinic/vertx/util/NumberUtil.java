package dev.myclinic.vertx.util;

import java.text.NumberFormat;

/**
 * Created by hangil on 2017/06/11.
 */
public class NumberUtil {
    private static NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public static String formatNumber(int number){
        return numberFormat.format(number);
    }
    public static String formatNumber(double number){ return numberFormat.format(number); }

}

package dev.myclinic.vertx.util;

import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static Pattern trimPattern = Pattern.compile("^[\\s　]+|[\\s　]+$");

    private StringUtil() {
    }

    public static String transliterate(String src, IntUnaryOperator converter) {
        return src.codePoints().map(converter)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static int kanjiToDigit(int codePoint) {
        switch (codePoint) {
            case '０':
                return '0';
            case '１':
                return '1';
            case '２':
                return '2';
            case '３':
                return '3';
            case '４':
                return '4';
            case '５':
                return '5';
            case '６':
                return '6';
            case '７':
                return '7';
            case '８':
                return '8';
            case '９':
                return '9';
            default:
                return codePoint;
        }
    }

    public static int digitToKanji(int codePoint) {
        switch (codePoint) {
            case '0':
                return '０';
            case '1':
                return '１';
            case '2':
                return '２';
            case '3':
                return '３';
            case '4':
                return '４';
            case '5':
                return '５';
            case '6':
                return '６';
            case '7':
                return '７';
            case '8':
                return '８';
            case '9':
                return '９';
            default:
                return codePoint;
        }
    }

    public static String trimSpaces(String s) {
        Matcher matcher = trimPattern.matcher(s);
        return matcher.replaceAll("");
    }

    public static String padLeft(char ch, String orig, int requiredLength){
        int n = requiredLength - orig.length();
        if( n > 0 ){
            StringBuilder builder = new StringBuilder(n);
            for(int i=0;i<n;i++){
                builder.append(ch);
            }
            return builder.toString() + orig;
        } else {
            return orig;
        }
    }

}

package dev.myclinic.vertx.drawerform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

class TextMetrics {

    TextMetrics() {

    }

    private boolean isHankaku(int code) {
        return (code >= 0xff61 && code <= 0xff64) ||
                (code >= 0xff65 && code <= 0xff9f) ||
                (code >= 0xffa0 && code <= 0xffdc) ||
                (code >= 0xffe8 && code <= 0xffee);
    }

    private double calcCharWidth(int code, double fontSize) {
        return (code < 256 || isHankaku(code)) ? fontSize / 2 : fontSize;
    }

    List<Double> measureChars(String str, double fontSize) {
        return str.codePoints().mapToDouble(code -> calcCharWidth(code, fontSize))
                .boxed().collect(toList());
    }

    private List<Double> composeXs(List<Double> mes, double left, double extraSpace) {
        List<Double> xs = new ArrayList<>();
        for (Double cw : mes) {
            xs.add(left);
            left += cw + extraSpace;
        }
        return xs;
    }

    private List<Double> composeYs(int nchar, double top, double fontSize, double extraSpace) {
        List<Double> ys = new ArrayList<>();
        for (int i = 0; i < nchar; i++) {
            ys.add(top);
            top += fontSize + extraSpace;
        }
        return ys;
    }

    private String codePointsToString(List<Integer> codePoints){
        int[] codes = codePoints.stream().mapToInt(c -> c).toArray();
        return new String(codes, 0, codes.length);
    }

    List<String> breakLine(String line, double fontSize, double lineWidth){
        if( line.isEmpty() ){
            return Collections.singletonList(line);
        }
        List<String> lines = new ArrayList<>();
        List<Integer> curCodePoints = new ArrayList<>();
        double curWidth = 0;
        for(int codePoint: line.codePoints().toArray()){
            double cw = calcCharWidth(codePoint, fontSize);
            if( curWidth > 0 && curWidth + cw > lineWidth ){
                lines.add(codePointsToString(curCodePoints));
                curCodePoints.clear();
                curWidth = 0;
            }
            curCodePoints.add(codePoint);
            curWidth += cw;
        }
        if( curCodePoints.size() > 0 ){
            lines.add(codePointsToString(curCodePoints));
        }
        return lines;
    }

}

package dev.myclinic.vertx.drawer;

import java.util.ArrayList;
import java.util.List;

public class LineBreaker {

    public static List<String> breakLine(String line, double fontSize, double lineWidth){
        LineBreaker breaker = new LineBreaker(fontSize, lineWidth);
        breaker.breakToLines(line);
        return breaker.getLines();
    }

    private double lineWidth;
    private double fontSize;
    private List<String> lines = new ArrayList<>();
    private List<Integer> curCodePoints = new ArrayList<>();
    private double curWidth;

    private LineBreaker(double fontSize, double lineWidth){
        this.fontSize = fontSize;
        this.lineWidth = lineWidth;
    }

    public void breakToLines(String line){
        if( line == null || line.isEmpty() ){
            lines.add("");
            return;
        }
        line.codePoints().forEach(this::addCode);
        if( curCodePoints.size() > 0 ){
            flushLine();
        }
    }

    public List<String> getLines(){
        return lines;
    }

    public void addCode(int code){
        double cw = charWidth(code);
        double width = curWidth + cw;
        if( width > lineWidth && curCodePoints.size() > 0 ){
            flushLine();
        }
        curCodePoints.add(code);
        curWidth += cw;
    }

    private void flushLine(){
        int[] codes = curCodePoints.stream().mapToInt(c -> c).toArray();
        String line = new String(codes, 0, codes.length);
        lines.add(line);
        curWidth = 0;
        curCodePoints.clear();
    }

    private double charWidth(int code){
        return ( code < 256 || isHankaku(code) ) ? fontSize/2 : fontSize;
    }

    private boolean isHankaku(int code){
        return (code >= 0xff61 && code <= 0xff64) ||
                (code >= 0xff65 && code <= 0xff9f) ||
                (code >= 0xffa0 && code <= 0xffdc) ||
                (code >= 0xffe8 && code <= 0xffee);
    }

}

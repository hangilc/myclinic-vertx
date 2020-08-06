package dev.myclinic.vertx.drawerform;

import dev.myclinic.vertx.drawer.DrawerConsts;
import dev.myclinic.vertx.drawer.OpCreatePen;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class FormCompiler extends AffineCompiler {

    private String currentFont = null;
    private final Map<String, Double> fontSizeMap = new HashMap<>();
    private final TextMetrics textMetrics = new TextMetrics();

    public static final int Bold = DrawerConsts.FontWeightBold;

    public FormCompiler() {

    }

    @Override
    public void createFont(String name, String fontName, double size, int weight, boolean italic) {
        super.createFont(name, fontName, size, weight, italic);
        fontSizeMap.put(name, size);
    }

    public void createFont(String name, String fontName, double size){
        createFont(name, fontName, size, DrawerConsts.FontWeightNormal, false);
    }

    public void setFont(String font){
        if( currentFont == null || !currentFont.equals(font) ){
            super.setFont(font);
            this.currentFont = font;
        }
    }

    public void createPen(String name, int red, int green, int blue, double width){
        createPen(name, red, green, blue, width, Collections.emptyList());
    }

    public double getCurrentFontSize(){
        return fontSizeMap.get(currentFont);
    }

    public void box(Box box){
        moveTo(box.getLeft(), box.getTop());
        lineTo(box.getRight(), box.getTop());
        lineTo(box.getRight(), box.getBottom());
        lineTo(box.getLeft(), box.getBottom());
        lineTo(box.getLeft(), box.getTop());
    }

    public static List<Double> addExtraSpaces(List<Double> mes, double spc){
        return mes.stream().map(cw -> cw + spc).collect(toList());
    }

    public Box textAt(String text, double x, double y, HAlign halign, VAlign valign){
        return textAt(text, x, y, halign, valign, null);
    }

    public static Function<List<Double>, List<Double>> spaceAdder(double space){
        return mes -> {
            List<Double> res = new ArrayList<>();
            for(int i=0;i<mes.size();i++){
                double m = mes.get(i);
                if( i != mes.size() - 1 ){
                    m += space;
                }
                res.add(m);
            }
            return res;
        };
    }

    public Box textAt(String text, double x, double y, HAlign halign, VAlign valign,
                      Function<List<Double>, List<Double>> mesModifier){
        List<Double> mes = textMetrics.measureChars(text, getCurrentFontSize());
        if( mesModifier != null ){
            mes = mesModifier.apply(mes);
        }
        double width = sum(mes);
        double left;
        switch(halign){
            case Center: left = x - width / 2; break;
            case Right: left = x - width; break;
            default: left = x; break;
        }
        double height = getCurrentFontSize();
        double top;
        switch(valign){
            case Center: top = y - height / 2; break;
            case Bottom: top = y - height; break;
            default: top = y; break;
        }
        drawChars(text, composeXs(left, mes), composeYs(top));
        return new Box(left, top, left + width, top + height);
    }

    public Box paraIn(String text, Box box, double leading){
        double fontSize = getCurrentFontSize();
        List<String> lines = breakParagraphLine(text, fontSize, box.getWidth());
        List<List<Double>> mess = lines.stream().map(line -> textMetrics.measureChars(line, fontSize))
                .collect(toList());
        double width = mess.stream().mapToDouble(this::sum).max().orElse(0);
        double y = box.getTop();
        double left = box.getLeft();
        for(int i=0;i<lines.size();i++){
            String line = lines.get(i);
            List<Double> mes = mess.get(i);
            textAt(line, left, y, HAlign.Left, VAlign.Top);
            y += fontSize + leading;
        }
        double height = fontSize * lines.size();
        if( lines.size() >= 2 ){
            height += leading + (lines.size() - 1);
        }
        return new Box(left, box.getTop(), left + width, box.getTop() + height);
    }

    public Box paraIn(String text, Box box){
        return paraIn(text, box, 0);
    }

    private List<String> splitToLines(String text){
        return List.of(text.split("\\r?\n"));
    }

    private List<String> breakParagraphLine(String text, double fontSize, double width){
        List<String> lines = new ArrayList<>();
        for(String s: splitToLines(text)){
            lines.addAll(textMetrics.breakLine(s, fontSize, width));
        }
        return lines;
    }

    private List<Double> composeXs(double left, List<Double> mes) {
        List<Double> xs = new ArrayList<>();
        for (Double me : mes) {
            xs.add(left);
            left += me;
        }
        return xs;
    }

    private List<Double> composeYs(double top){
        return Collections.singletonList(top);
    }

    private double sum(List<Double> values){
        if( values == null ){
            return 0;
        }
        return values.stream().mapToDouble(d -> d).sum();
    }

}

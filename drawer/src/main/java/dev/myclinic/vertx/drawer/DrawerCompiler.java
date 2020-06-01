package dev.myclinic.vertx.drawer;

import dev.myclinic.vertx.drawer.render.Renderable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DrawerCompiler {

    public enum HAlign {
        Left, Center, Right
    }

    public enum VAlign {
        Top, Center, Bottom
    }

    public static class TextAtOpt {
        public double extraSpace;
    }

    public enum TextInEvenColumnsJustification {
        Left, Right
    }

    private List<Op> ops = new ArrayList<>();
    private Map<String, Double> fontMap = new HashMap<>();
    private String currentFont;
    private Stack<String> fontStack = new Stack<>();
    private Map<String, Point> pointDict = new HashMap<>();
    private Map<String, Box> boxDict = new HashMap<>();

    public DrawerCompiler() {

    }

    public List<Op> getOps() {
        return ops;
    }

    public void clearOps(){
        this.ops = new ArrayList<>();
    }

    public void setOps(List<Op> ops){
        this.ops = ops;
    }

    public void moveTo(double x, double y) {
        ops.add(new OpMoveTo(x, y));
    }

    public void lineTo(double x, double y) {
        ops.add(new OpLineTo(x, y));
    }

    public void line(double x1, double y1, double x2, double y2) {
        moveTo(x1, y1);
        lineTo(x2, y2);
    }

    public void rectangle(double left, double top, double right, double bottom) {
        moveTo(left, top);
        lineTo(right, top);
        lineTo(right, bottom);
        lineTo(left, bottom);
        lineTo(left, top);
    }

    public void box(Box box) {
        rectangle(box.getLeft(), box.getTop(), box.getRight(), box.getBottom());
    }

    public void createFont(String name, String fontName, double size, int weight, boolean italic) {
        ops.add(new OpCreateFont(name, fontName, size, weight, italic));
        fontMap.put(name, size);
    }

    public void createFont(String name, String fontName, double size) {
        createFont(name, fontName, size, 0, false);
    }

    public void pushFont() {
        fontStack.push(currentFont);
    }

    public void popFont() {
        setFont(fontStack.pop());
    }

    public void setFont(String name) {
        if( !Objects.equals(currentFont, name) ){
            ops.add(new OpSetFont(name));
            currentFont = name;
        }
    }

    public void setFontPushing(String name) {
        pushFont();
        setFont(name);
    }

    public String getCurrentFont() {
        return currentFont;
    }

    public double getCurrentFontSize() {
        return fontMap.get(currentFont);
    }

    public double getFontSizeFor(String fontName) {
        return fontMap.get(fontName);
    }

    public void textAt(String text, double x, double y, HAlign halign, VAlign valign, TextAtOpt opt){
        if (text == null || text.isEmpty()) {
            return;
        }
        double extraSpace = opt == null ? 0 : opt.extraSpace;
        List<Double> mes = doMeasureChars(text, getCurrentFontSize());
        double totalWidth = mes.stream().reduce((a, b) -> a + b).orElse(0.0) + extraSpace * (text.length() - 1);
        double left, top;
        switch (halign) {
            case Left:
                left = x;
                break;
            case Center:
                left = x - totalWidth / 2.0;
                break;
            case Right:
                left = x - totalWidth;
                break;
            default:
                throw new RuntimeException("invalid halign: " + halign);
        }
        switch (valign) {
            case Top:
                top = y;
                break;
            case Center:
                top = y - getCurrentFontSize() / 2;
                break;
            case Bottom:
                top = y - getCurrentFontSize();
                break;
            default:
                throw new RuntimeException("invalid valign: " + valign);
        }
        List<Double> xs = composeXs(mes, left, extraSpace);
        List<Double> ys = Collections.singletonList(top);
        ops.add(new OpDrawChars(text, xs, ys));
    }

    public void textAt(String text, double x, double y, HAlign halign, VAlign valign) {
        textAt(text, x, y, halign, valign, null);
    }

    public void textAt(String text, Point p, HAlign halign, VAlign valign) {
        textAt(text, p.getX(), p.getY(), halign, valign);
    }

    public void textAtJustified(String text, double left, double right, double y, VAlign valign) {
        if (text == null || text.isEmpty()) {
            return;
        }
        List<Double> mes = doMeasureChars(text, getCurrentFontSize());
        double totalWidth = mes.stream().reduce((a, b) -> a + b).orElse(0.0);
        if (text.length() < 2) {
            textAt(text, left, y, HAlign.Left, valign);
        } else {
            double top;
            switch (valign) {
                case Top:
                    top = y;
                    break;
                case Center:
                    top = y - getCurrentFontSize() / 2;
                    break;
                case Bottom:
                    top = y - getCurrentFontSize();
                    break;
                default:
                    throw new RuntimeException("invalid valign: " + valign);
            }
            double extra = ((right - left) - totalWidth) / (text.length() - 1);
            List<Double> xs = composeXs(mes, left, extra);
            List<Double> ys = Collections.singletonList(top);
            ops.add(new OpDrawChars(text, xs, ys));
        }
    }

    public void textAtVert(String text, double x, double y, HAlign halign, VAlign valign) {
        List<Double> mes = doMeasureChars(text, getCurrentFontSize());
        double totalHeight = getCurrentFontSize() * text.length();
        List<Double> xs = mes.stream().map(cw -> {
            switch (halign) {
                case Left:
                    return x;
                case Center:
                    return x - cw / 2;
                case Right:
                    return x - cw;
                default:
                    throw new RuntimeException("unknown halign: " + halign);
            }
        }).collect(Collectors.toList());
        double top;
        switch (valign) {
            case Top:
                top = y;
                break;
            case Center:
                top = y - totalHeight / 2;
                break;
            case Bottom:
                top = y - totalHeight;
                break;
            default:
                throw new RuntimeException("invalid valign: " + valign);
        }
        List<Double> ys = composeYs(text.length(), top, getCurrentFontSize(), 0);
        ops.add(new OpDrawChars(text, xs, ys));
    }

    public void textAtVertJustified(String text, double x, double top, double bottom, HAlign halign) {
        if (text == null || text.isEmpty()) {
            return;
        }
        List<Double> mes = doMeasureChars(text, getCurrentFontSize());
        if (text.length() < 2) {
            textAt(text, x, top, halign, VAlign.Top);
            return;
        }
        List<Double> xs = mes.stream().map(cw -> {
            switch (halign) {
                case Left:
                    return x;
                case Center:
                    return x - cw / 2;
                case Right:
                    return x - cw;
                default:
                    throw new RuntimeException("unknown halign: " + halign);
            }
        }).collect(Collectors.toList());
        double totalHeight = getCurrentFontSize() * text.length();
        double extra = ((bottom - top) - totalHeight) / (text.length() - 1);
        List<Double> ys = composeYs(text.length(), top, getCurrentFontSize(), extra);
        ops.add(new OpDrawChars(text, xs, ys));
    }

    public void textIn(String text, Box box, HAlign halign, VAlign valign){
        double x, y;
        switch (halign) {
            case Left:
                x = box.getLeft();
                break;
            case Center:
                x = box.getCx();
                break;
            case Right:
                x = box.getRight();
                break;
            default:
                throw new RuntimeException("invalid halign:" + halign);
        }
        switch (valign) {
            case Top:
                y = box.getTop();
                break;
            case Center:
                y = box.getCy();
                break;
            case Bottom:
                y = box.getBottom();
                break;
            default:
                throw new Error("invalid valign: " + valign);
        }
        textAt(text, x, y, halign, valign, null);
    }

    public void textInVert(String text, Box box, HAlign halign, VAlign valign) {
        double x, y;
        switch (halign) {
            case Left:
                x = box.getLeft();
                break;
            case Center:
                x = box.getCx();
                break;
            case Right:
                x = box.getRight();
                break;
            default:
                throw new RuntimeException("invalid halign:" + halign);
        }
        switch (valign) {
            case Top:
                y = box.getTop();
                break;
            case Center:
                y = box.getCy();
                break;
            case Bottom:
                y = box.getBottom();
                break;
            default:
                throw new Error("invalid valign: " + valign);
        }
        textAtVert(text, x, y, halign, valign);
    }

    public void textInJustified(String text, Box box, VAlign valign) {
        double y;
        switch (valign) {
            case Top:
                y = box.getTop();
                break;
            case Center:
                y = box.getCy();
                break;
            case Bottom:
                y = box.getBottom();
                break;
            default:
                throw new Error("invalid valign: " + valign);
        }
        textAtJustified(text, box.getLeft(), box.getRight(), y, valign);
    }

    public void textInVertJustified(String text, Box box, HAlign halign) {
        double x;
        switch (halign) {
            case Left:
                x = box.getLeft();
                break;
            case Center:
                x = box.getCx();
                break;
            case Right:
                x = box.getRight();
                break;
            default:
                throw new RuntimeException("invalid halign:" + halign);
        }
        textAtVertJustified(text, x, box.getTop(), box.getBottom(), halign);
    }

    public void textInEvenColumns(String text, Box box, int nCols, TextInEvenColumnsJustification justifyTo) {
        if (text.length() > nCols) {
            System.out.println("text is longer than columns");
        }
        int n = Math.min(text.length(), nCols);
        Box[] cols = box.splitToEvenColumns(nCols);
        if (justifyTo == TextInEvenColumnsJustification.Left) {
            for (int i = 0; i < n; i++) {
                textIn(text.substring(i, i + 1), cols[i], HAlign.Center, VAlign.Center);
            }
        } else {
            int nPad = nCols - n;
            for (int i = 0; i < n; i++) {
                textIn(text.substring(i, i + 1), cols[i + nPad], HAlign.Center, VAlign.Center);
            }
        }
    }

    public static class TextInBoundedOptions {
        public List<String> smallerFonts;
        public String multilineFont;
        public HAlign multilineHAlign;
    }

    public boolean textInBounded(String text, Box box, HAlign halign, VAlign valign,
                                 TextInBoundedOptions opts){
        Measure mes = measureText(text);
        if( mes.cx <= box.getWidth() ){
            textIn(text, box, halign, valign);
            return true;
        }
        if( opts != null ) {
            if (opts.smallerFonts != null) {
                String fontSave = getCurrentFont();
                try {
                    for (String fontName : opts.smallerFonts) {
                        setFont(fontName);
                        mes = measureText(text);
                        if (mes.cx <= box.getWidth()) {
                            textIn(text, box, halign, valign);
                            return true;
                        }
                    }
                } finally {
                    setFont(fontSave);
                }
            }
            if( opts.multilineFont != null ){
                String fontSave = getCurrentFont();
                try {
                    setFont(opts.multilineFont);
                    List<String> lines = breakLine(text, box.getWidth());
                    double leading = 0;
                    double height = calcTotalHeight(lines.size(), getCurrentFontSize(), leading);
                    if( height <= box.getHeight() ){
                        HAlign multilineHAlign = opts.multilineHAlign != null ?
                                opts.multilineHAlign : halign;
                        multilineText(lines, box, multilineHAlign, valign, leading);
                        return true;
                    }
                } finally {
                    setFont(fontSave);
                }
            }
        }
        return false;
    }

    private double getStartX(Box box, HAlign halign, Supplier<Double> widthSupplier) {
        switch (halign) {
            case Left:
                return box.getLeft();
            case Center:
                return box.getCx() - widthSupplier.get() / 2.0;
            case Right:
                return box.getRight() - widthSupplier.get();
            default:
                throw new RuntimeException("Invalid halign value.");
        }
    }

    private double getAnchorX(Box box, HAlign halign) {
        switch (halign) {
            case Left:
                return box.getLeft();
            case Center:
                return box.getCy();
            case Right:
                return box.getRight();
            default:
                throw new RuntimeException("Invalid halign value.");
        }
    }

    private double getAnchorY(Box box, VAlign valign) {
        switch (valign) {
            case Top:
                return box.getTop();
            case Center:
                return box.getCy();
            case Bottom:
                return box.getBottom();
            default:
                throw new RuntimeException("Invalid valign value.");
        }
    }

    // returns last x
    public double render(List<Renderable> items, Box box, HAlign halign, VAlign valign) {
        double y = getAnchorY(box, valign);
        double x = getStartX(box, halign, () ->
                items.stream().mapToDouble(r -> r.calcWidth(this)).sum());
        for (Renderable r : items) {
            r.render(this, x, y, valign);
            x += r.calcWidth(this);
        }
        return x;
    }

    public void setTextColor(int red, int green, int blue) {
        ops.add(new OpSetTextColor(red, green, blue));
    }

    public void createPen(String name, int red, int green, int blue, double width, int penStyle) {
        ops.add(new OpCreatePen(name, red, green, blue, width, penStyle));

    }

    public void createPen(String name, int red, int green, int blue, double width) {
        createPen(name, red, green, blue, width, OpCreatePen.PS_SOLID);
    }

    public void createPen(String name, int red, int green, int blue) {
        createPen(name, red, green, blue, 0.1);
    }

    public void setPen(String name) {
        ops.add(new OpSetPen(name));
    }

    public void setPoint(String name, double x, double y) {
        pointDict.put(name, new Point(x, y));
    }

    public Point getPoint(String name) {
        return pointDict.get(name);
    }

    public void setBox(String name, Box box) {
        boxDict.put(name, box);
    }

    public Box getBox(String name) {
        return boxDict.get(name);
    }

    public void frameLeft(Box box) {
        line(box.getLeft(), box.getBottom(), box.getLeft(), box.getTop());
    }

    public void frameTop(Box box) {
        line(box.getLeft(), box.getTop(), box.getRight(), box.getTop());
    }

    public void frameRight(Box box) {
        line(box.getRight(), box.getTop(), box.getRight(), box.getBottom());
    }

    public void frameBottom(Box box) {
        line(box.getRight(), box.getBottom(), box.getLeft(), box.getBottom());
    }

    public void frameCells(Box[][] cells) {
        for (Box[] row : cells) {
            for (Box cell : row) {
                box(cell);
            }
        }
    }

    public void frameRightOfNthColumn(Box[][] cells, int iCol, double dx) {
        double x = cells[0][iCol].getRight() + dx;
        double top = cells[0][0].getTop();
        double bottom = cells[cells.length - 1][0].getBottom();
        line(x, top, x, bottom);
    }

    public void frameInnerColumnBorders(Box box, int nCol) {
        double left = box.getLeft();
        double top = box.getTop();
        double bottom = box.getBottom();
        double cw = box.getWidth() / nCol;
        for (int i = 1; i < nCol; i++) {
            double x = left + cw * i;
            line(x, top, x, bottom);
        }
    }

    public void frameInnerColumnBorders(Box[] cols) {
        for (int i = 1; i < cols.length; i++) {
            frameLeft(cols[i]);
        }
    }

    public void frameInnerColumnBorders(Box[][] cells) {
        int nCol = cells[0].length;
        Box[] firstRow = cells[0];
        double top = firstRow[0].getTop();
        double bottom = cells[cells.length - 1][0].getBottom();
        for (int i = 1; i < firstRow.length; i++) {
            Box cell = firstRow[i];
            double x = cell.getLeft();
            line(x, top, x, bottom);
        }
    }

    public void frameInnerRowBorders(Box[] rows) {
        for (int i = 1; i < rows.length; i++) {
            frameTop(rows[i]);
        }
    }

    public List<String> breakLine(String line, double lineWidth) {
        return LineBreaker.breakLine(line, getCurrentFontSize(), lineWidth);
    }

    public static class MultiTextResult {
        public boolean allDrawn;
        public int linesRendered;
        public double vertOffsetInBox; // vertical offset of bottom of last line relative to box

        public MultiTextResult(boolean allDrawn, int linesRendered, double vertOffsetInBox) {
            this.allDrawn = allDrawn;
            this.linesRendered = linesRendered;
            this.vertOffsetInBox = vertOffsetInBox;
        }
    }

    // returns number of liines that are successfully drawn
    public MultiTextResult tryMultilineText(Collection<String> lines, Box box, HAlign halign, VAlign valign,
                                            double leading){
        if (lines == null || lines.size() == 0) {
            return new MultiTextResult(true, 0, 0);
        }
        int nLines = lines.size();
        double y;
        switch (valign) {
            case Top:
                y = box.getTop();
                break;
            case Center:
                y = box.getTop() + (box.getHeight() - calcTotalHeight(nLines, getCurrentFontSize(), leading)) / 2;
                break;
            case Bottom:
                y = box.getTop() + box.getHeight() - calcTotalHeight(nLines, getCurrentFontSize(), leading);
                break;
            default:
                throw new RuntimeException("invalid valign: " + valign);
        }
        double x;
        switch (halign) {
            case Left:
                x = box.getLeft();
                break;
            case Center:
                x = box.getCx();
                break;
            case Right:
                x = box.getRight();
                break;
            default:
                throw new RuntimeException("invalid halign: " + halign);
        }
        int index = 0;
        for (String line : lines) {
            double remain = box.getBottom() - y;
            if( getCurrentFontSize() <= remain ){
                if( index != 0 ){
                    y += leading;
                }
                textAt(line, x, y, halign, VAlign.Top);
                y += getCurrentFontSize();
                index += 1;
            } else {
                break;
            }
        }
        return new MultiTextResult(index == lines.size(), index, box.getTop() - y);
    }

    public double multilineText(Collection<String> lines, Box box, HAlign halign, VAlign valign, double leading) {
        if (lines == null || lines.size() == 0) {
            return box.getTop();
        }
        int nLines = lines.size();
        double y;
        switch (valign) {
            case Top:
                y = box.getTop();
                break;
            case Center:
                y = box.getTop() + (box.getHeight() - calcTotalHeight(nLines, getCurrentFontSize(), leading)) / 2;
                break;
            case Bottom:
                y = box.getTop() + box.getHeight() - calcTotalHeight(nLines, getCurrentFontSize(), leading);
                break;
            default:
                throw new RuntimeException("invalid valign: " + valign);
        }
        double x;
        switch (halign) {
            case Left:
                x = box.getLeft();
                break;
            case Center:
                x = box.getCx();
                break;
            case Right:
                x = box.getRight();
                break;
            default:
                throw new RuntimeException("invalid halign: " + halign);
        }
        for (String line : lines) {
            textAt(line, x, y, halign, VAlign.Top);
            y += getCurrentFontSize() + leading;
        }
        return y - leading;
    }

    public double multilineText(String[] lines, Box box, HAlign halign, VAlign valign, double leading) {
        return multilineText(Arrays.asList(lines), box, halign, valign, leading);
    }

    public double paragraph(String src, Box box, HAlign halign, VAlign valign, double leading) {
        String[] para = src.split("\\r?\n");
        List<String> lines = new ArrayList<>();
        double width = box.getWidth();
        for (String p : para) {
            List<String> bl = breakLine(p, width);
            lines.addAll(bl);
        }
        return multilineText(lines, box, halign, valign, leading);
    }

    public double calcTotalHeight(int nLines, double fontSize, double leading) {
        if (nLines == 0) {
            return 0;
        } else {
            return nLines * fontSize + leading * (nLines - 1);
        }
    }

    public static class Measure {
        public double cx;
        public double cy;
    }

    public Measure measureText(String text) {
        Measure mes = new Measure();
        mes.cx = doMeasureChars(text, getCurrentFontSize()).stream().reduce((a, b) -> a + b).orElse(0.0);
        mes.cy = getCurrentFontSize();
        return mes;
    }

    public double calcTextWidth(String text) {
        return calcTextWidth(text, getCurrentFontSize());
    }

    public double calcTextWidth(String text, double fontSize) {
        return text.codePoints().mapToDouble(code -> doCalcCharWidth(code, fontSize)).sum();
    }

    public void circle(double cx, double cy, double r){
        ops.add(new OpCircle(cx, cy, r));
    }

    public void circle(Point c, double r){
        circle(c.getX(), c.getY(), r);
    }

    private static List<Double> doMeasureChars(String str, double fontSize) {
        return str.codePoints().mapToDouble(code -> doCalcCharWidth(code, fontSize)).boxed().collect(Collectors.toList());
    }

    private static double doCalcCharWidth(int code, double fontSize) {
        return (code < 256 || isHankaku(code)) ? fontSize / 2 : fontSize;
    }

    private static boolean isHankaku(int code) {
        return (code >= 0xff61 && code <= 0xff64) ||
                (code >= 0xff65 && code <= 0xff9f) ||
                (code >= 0xffa0 && code <= 0xffdc) ||
                (code >= 0xffe8 && code <= 0xffee);
    }

    private static List<Double> composeXs(List<Double> mes, double left, double extraSpace) {
        List<Double> xs = new ArrayList<>();
        for (Double cw : mes) {
            xs.add(left);
            left += cw + extraSpace;
        }
        return xs;
    }

    private static List<Double> composeYs(int nchar, double top, double fontSize, double extraSpace) {
        List<Double> ys = new ArrayList<>();
        for (int i = 0; i < nchar; i++) {
            ys.add(top);
            top += fontSize + extraSpace;
        }
        return ys;
    }

}

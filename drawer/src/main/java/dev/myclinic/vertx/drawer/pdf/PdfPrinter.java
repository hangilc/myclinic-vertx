package dev.myclinic.vertx.drawer.pdf;

import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import dev.myclinic.vertx.drawer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfPrinter {

    private static Logger logger = LoggerFactory.getLogger(PdfPrinter.class);

    private final double paperWidth;
    private final double paperHeight;
    private boolean inText;
    private BaseFont textFont;
    private float textSize;
    private float ascendor;
    private BaseColor textColor = new BaseColor(0, 0, 0);
    private BaseColor strokeColor = new BaseColor(0, 0, 0);
    private float strokeWidth = 1;
    private int strokeStyle = OpCreatePen.PS_SOLID;
    private double shrinkMargin = 0;

    public PdfPrinter() {
        this(PaperSize.A4);
    }

    public PdfPrinter(PaperSize paperSize){
        this(paperSize.getWidth(), paperSize.getHeight());
    }

    public PdfPrinter(double paperWidth, double paperHeight){
        this.paperWidth = milliToPoint(paperWidth);
        this.paperHeight = milliToPoint(paperHeight);
    }

    public void setShrink(double margin){
        this.shrinkMargin = margin;
    }

    private double milliToPoint(double milli) {
        double inch = milli * 0.0393701;
        return inch * 72.0;
    }

    private static class FontResourceData {
        public String location;
        public String encoding;

        public FontResourceData(String location, String encoding) {
            this.location = location;
            this.encoding = encoding;
        }
    }

    private static final Map<String, FontResourceData> fontResourceMap = new HashMap<>();

    static {
        FontResourceData mincho = new FontResourceData("C:\\Windows\\Fonts\\msmincho.ttc,0", "Identity-H");
        fontResourceMap.put("MS Mincho", mincho);
        FontResourceData gothic = new FontResourceData("C:\\Windows\\Fonts\\msgothic.ttc,0", "Identity-H");
        fontResourceMap.put("MS Gothic", gothic);
    }

    private float getAscendorRate(String fontProgram) throws IOException {
        FontProgram fp = FontProgramFactory.createFont("C:\\Windows\\Fonts\\msmincho.ttc,0");
        FontMetrics fm = fp.getFontMetrics();
        return (float)(fm.getTypoAscender() / 1000.0);
    }

    private static class BaseFontData {
        public BaseFont baseFont;
        public float ascendorRate;

        public BaseFontData(BaseFont baseFont, float ascendorRate) {
            this.baseFont = baseFont;
            this.ascendorRate = ascendorRate;
        }
    }

    private static class DrawerFont {
        public BaseFont font;
        public float size;
        public float ascendor;

        public DrawerFont(BaseFont font, float size, float ascendor) {
            this.font = font;
            this.size = size;
            this.ascendor = ascendor;
        }

    }

    private static class StrokeData {
        public BaseColor color;
        public float width;
        public int style;

        public StrokeData(BaseColor color, float width, int style) {
            this.color = color;
            this.width = width;
            this.style = style;
        }
    }

    private float getX(double milliX) {
        double pointX = milliToPoint(milliX);
        return (float)pointX;
    }

    private float getY(double milliY){
        double pointY = milliToPoint(milliY);
        return (float)(paperHeight - pointY);
    }

    private void applyShrink(PdfContentByte cb, double margin){
        double uMargin = milliToPoint(margin);
        double scale = (paperWidth - 2 * uMargin) / paperWidth;
        cb.concatCTM(scale, 0, 0, scale, uMargin, uMargin);
    }

    public void print(List<List<Op>> pages, OutputStream outStream) throws Exception {
        Map<String, BaseFontData> fontMap = new HashMap<>();
        Map<String, DrawerFont> drawerFontMap = new HashMap<>();
        Map<String, StrokeData> strokeMap = new HashMap<>();
        Document doc = new Document(new Rectangle((float)paperWidth, (float)paperHeight), 0, 0, 0, 0);
        PdfWriter pdfWriter = PdfWriter.getInstance(doc, outStream);
        doc.open();
        PdfContentByte cb = pdfWriter.getDirectContent();
        for (int i = 0; i < pages.size(); i++) {
            if (i != 0) {
                doc.newPage();
            }
            if( shrinkMargin != 0.0 ){
                applyShrink(cb, shrinkMargin);
            }
            doc.add(new Chunk(""));
            List<Op> ops = pages.get(i);
            for (Op op : ops) {
                switch (op.getOpCode()) {
                    case CreateFont: {
                        OpCreateFont opCreateFont = (OpCreateFont) op;
                        String name = opCreateFont.getName();
                        if (!drawerFontMap.containsKey(name)) {
                            String fontName = opCreateFont.getFontName();
                            if (!fontMap.containsKey(fontName)) {
                                FontResourceData fr = fontResourceMap.getOrDefault(fontName, null);
                                if (fr == null) {
                                    throw new RuntimeException("Cannot find font: " + fontName);
                                }
                                BaseFont font = BaseFont.createFont(fr.location, fr.encoding,
                                        BaseFont.EMBEDDED);
                                fontMap.put(fontName, new BaseFontData(font, getAscendorRate(fr.location)));
                            }
                            BaseFontData fontData = fontMap.get(fontName);
                            float size = (float) milliToPoint(opCreateFont.getSize());
                            float ascendor = size * fontData.ascendorRate;
                            drawerFontMap.put(name, new DrawerFont(fontData.baseFont, size, ascendor));
                        }
                        break;
                    }
                    case SetFont: {
                        OpSetFont opSetFont = (OpSetFont) op;
                        String name = opSetFont.getName();
                        DrawerFont dfont = drawerFontMap.getOrDefault(name, null);
                        if (dfont == null) {
                            throw new RuntimeException("Unknown font: " + name);
                        }
                        textFont = dfont.font;
                        textSize = dfont.size;
                        ascendor = dfont.ascendor;
                        if (inText) {
                            cb.setFontAndSize(dfont.font, dfont.size);
                        }
                        break;
                    }
                    case DrawChars: {
                        if( !inText ){
                            cb.stroke();
                            cb.beginText();
                            cb.setFontAndSize(textFont, textSize);
                            cb.setColorFill(textColor);
                            inText = true;
                        }
                        OpDrawChars opDrawChars = (OpDrawChars) op;
                        String chars = opDrawChars.getChars();
                        List<Double> xs = opDrawChars.getXs();
                        List<Double> ys = opDrawChars.getYs();
                        for(int j=0;j<chars.length();j++){
                            String text = chars.substring(j, j+1);
                            double x = j < xs.size() ? xs.get(j) : xs.get(xs.size() - 1);
                            double y = j < ys.size() ? ys.get(j) : ys.get(ys.size() - 1);
                            cb.setTextMatrix(getX(x), getY(y) - ascendor);
                            cb.showText(text);
                        }
                        break;
                    }
                    case SetTextColor: {
                        OpSetTextColor opSetTextColor = (OpSetTextColor)op;
                        BaseColor baseColor = new BaseColor(
                                opSetTextColor.getR(),
                                opSetTextColor.getG(),
                                opSetTextColor.getB()
                        );
                        this.textColor = baseColor;
                        if( inText ){
                            cb.setColorFill(baseColor);
                        }
                        break;
                    }
                    case CreatePen: {
                        OpCreatePen opCreatePen = (OpCreatePen)op;
                        String name = opCreatePen.getName();
                        if( !strokeMap.containsKey(name) ){
                            BaseColor color = new BaseColor(opCreatePen.getR(), opCreatePen.getG(),
                                    opCreatePen.getB());
                            float width = (float)milliToPoint(opCreatePen.getWidth());
                            StrokeData data = new StrokeData(color, width, opCreatePen.getPenStyle());
                            strokeMap.put(name, data);
                        }
                        break;
                    }
                    case SetPen: {
                        OpSetPen opSetPen = (OpSetPen)op;
                        String name = opSetPen.getName();
                        StrokeData data = strokeMap.getOrDefault(name, null);
                        if( data == null ){
                            throw new RuntimeException("Cannot find pen: " + name);
                        }
                        strokeColor = data.color;
                        strokeWidth = data.width;
                        strokeStyle = data.style;
                        if( !inText ){
                            cb.setColorStroke(strokeColor);
                            cb.setLineWidth(strokeWidth);
                        }
                        break;
                    }
                    case MoveTo: {
                        if( inText ){
                            cb.endText();
                            inText = false;
                        }
                        cb.stroke();
                        OpMoveTo opMoveTo = (OpMoveTo)op;
                        float x = getX(opMoveTo.getX());
                        float y = getY(opMoveTo.getY());
                        cb.moveTo(x, y);
                        break;
                    }
                    case LineTo: {
                        OpLineTo opLineTo = (OpLineTo)op;
                        float x = getX(opLineTo.getX());
                        float y = getY(opLineTo.getY());
                        cb.lineTo(x, y);
                        break;
                    }
                    case Circle: {
                        if( inText ){
                            cb.endText();
                            inText = false;
                        }
                        cb.stroke();
                        OpCircle opCircle = (OpCircle)op;
                        float x = getX(opCircle.getCx());
                        float y = getY(opCircle.getCy());
                        float r = (float)milliToPoint(opCircle.getR());
                        cb.arc(x-r, y+r, x+r, y-r, 0, 360);
                        cb.stroke();
                        break;
                    }
                    default:
                        logger.warn("Unknown op in PdfPrinter (ignored): " + op.toString());
                        break;
                }
            }
            if (inText) {
                cb.endText();
                inText = false;
            } else {
                cb.stroke();
            }
        }
        doc.close();
    }

    public void print(List<List<Op>> pages, String savePath) throws Exception {
        OutputStream outStream;
        if( "-".equals(savePath) ){
            outStream = System.out;
        } else {
            outStream = new FileOutputStream(savePath);
        }
        print(pages, outStream);
    }

}

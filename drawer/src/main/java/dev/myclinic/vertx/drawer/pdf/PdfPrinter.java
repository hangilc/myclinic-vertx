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
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawer.hint.Hint;
import dev.myclinic.vertx.drawer.hint.HintParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfPrinter {

    private static final Logger logger = LoggerFactory.getLogger(PdfPrinter.class);

    private final double paperWidth;
    private final double paperHeight;
    private boolean inText;
    private final TextContext textContext = new TextContext();
    private final GraphicContext graphicContext = new GraphicContext();
    private double shrinkMargin = 0;

    public PdfPrinter() {
        this(PaperSize.A4);
    }

    public PdfPrinter(String paper){
        this(PaperSize.resolvePaperSize(paper));
    }

    public PdfPrinter(PaperSize paperSize) {
        this(paperSize.getWidth(), paperSize.getHeight());
    }

    public PdfPrinter(double paperWidth, double paperHeight) {
        this.paperWidth = milliToPoint(paperWidth);
        this.paperHeight = milliToPoint(paperHeight);
    }

    public void setShrink(double margin) {
        this.shrinkMargin = margin;
    }

    private double milliToPoint(double milli) {
        double inch = milli * 0.0393701;
        return inch * 72.0;
    }

    private float getAscendorRate(String fontProgram) throws IOException {
        //FontProgram fp = FontProgramFactory.createFont("C:\\Windows\\Fonts\\msmincho.ttc,0");
        FontProgram fp = FontProgramFactory.createFont(fontProgram);
        FontMetrics fm = fp.getFontMetrics();
        return (float) (fm.getTypoAscender() / 1000.0);
    }

    private float getX(double milliX) {
        double pointX = milliToPoint(milliX);
        return (float) pointX;
    }

    private float getY(double milliY) {
        double pointY = milliToPoint(milliY);
        return (float) (paperHeight - pointY);
    }

    private void applyShrink(PdfContentByte cb, double margin) {
        double uMargin = milliToPoint(margin);
        double scale = (paperWidth - 2 * uMargin) / paperWidth;
        cb.concatCTM(scale, 0, 0, scale, uMargin, uMargin);
    }

    private void beginTextMode(PdfContentByte cb) {
        cb.beginText();
        if (textContext.isInitialized()) {
            cb.setFontAndSize(textContext.getTextFont(), textContext.getTextSize());
            cb.setColorFill(textContext.getTextColor());
        }
    }

    private void endTextMode(PdfContentByte cb) {
        cb.endText();
    }

    private void beginGraphicMode(PdfContentByte cb) {
        if (graphicContext.isInitialized()) {
            cb.setColorStroke(graphicContext.getStrokeColor());
            cb.setLineWidth(graphicContext.getStrokeWidth());
            float[] strokeStyle = graphicContext.getStrokeStyle();
            if (strokeStyle == null || strokeStyle.length == 0) {
                cb.setLineDash(0);
            } else {
                cb.setLineDash(strokeStyle, 0);
            }
        }
    }

    private void endGraphicMode(PdfContentByte cb) {
        cb.stroke();
    }

    private void textMode(PdfContentByte cb) {
        if (!inText) {
            endGraphicMode(cb);
            beginTextMode(cb);
            inText = true;
        }
    }

    private void graphicMode(PdfContentByte cb) {
        if (inText) {
            endTextMode(cb);
            beginGraphicMode(cb);
            inText = false;
        }
    }

    public interface CustomRenderer {
        void render(DrawerCompiler c, Box box, String mark, String s, Hint hint);
    }

    public static class FormPageData {
        public int pageId;
        public Map<String, String> markTexts;
        public Map<String, CustomRenderer> customRenderers;
    }

    public void print(Form form, List<FormPageData> pageDataList, OutputStream outStream) throws Exception {
        List<Map<String, Hint>> compiledHints = new ArrayList<>();
        for (Page formPage : form.pages) {
            Map<String, Hint> ch = new HashMap<>();
            for (String key : formPage.hints.keySet()) {
                String src = formPage.hints.get(key);
                Hint h = HintParser.parse(src);
                ch.put(key, h);
            }
            compiledHints.add(ch);
        }
        DrawerCompiler c = new DrawerCompiler();
        c.importOps(form.setup);
        List<List<Op>> pageOps = new ArrayList<>();
        for (FormPageData data : pageDataList) {
            c.clearOps();
            Page formPage = form.pages.get(data.pageId);
            c.importOps(formPage.ops);
            Map<String, Hint> ch = compiledHints.get(data.pageId);
            for(String key: data.markTexts.keySet()){
                String s = data.markTexts.get(key);
                CustomRenderer cr = data.customRenderers.get(key);
                Box box = formPage.marks.get(key).toBox();
                Hint h = ch.get(key);
                if( cr != null ){
                    cr.render(c, box, key, s, h);
                } else {
                    Hint.render(c, box, s, h);
                }
            }
            pageOps.add(c.getOps());
        }
        print(form.setup, pageOps, outStream);
    }

    public interface Callback {
        void proc(PdfContentByte cb, int page, Runnable graphicMode, Runnable textMode)
                throws Exception;
    }

    public void print(List<Op> setup, List<List<Op>> pages, OutputStream outStream, Callback callback)
            throws Exception {
        if (pages.size() > 0) {
            List<Op> ops = new ArrayList<>();
            ops.addAll(setup);
            ops.addAll(pages.get(0));
            pages.set(0, ops);
        }
        print(pages, outStream, callback);
    }
    
    
    

    public void print(List<Op> setup, List<List<Op>> pages, OutputStream outStream)
            throws Exception {
        print(setup, pages, outStream, null);
    }

    public void print(List<List<Op>> pages, OutputStream outStream, Callback callback) throws Exception {
        Map<String, BaseFontData> fontMap = new HashMap<>();
        Map<String, DrawerFont> drawerFontMap = new HashMap<>();
        Map<String, StrokeData> strokeMap = new HashMap<>();
        Document doc = new Document(new Rectangle((float) paperWidth, (float) paperHeight), 0, 0, 0, 0);
        PdfWriter pdfWriter = PdfWriter.getInstance(doc, outStream);
        doc.open();
        PdfContentByte cb = pdfWriter.getDirectContent();
        for (int i = 0; i < pages.size(); i++) {
            if (i != 0) {
                if (callback != null) {
                    callback.proc(cb, i, () -> graphicMode(cb), () -> textMode(cb));
                }
                doc.newPage();
            }
            if (shrinkMargin != 0.0) {
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
                                FontResourceData fr = FontResourceData.fontResourceMap.getOrDefault(fontName, null);
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
                        textMode(cb);
                        endTextMode(cb);
                        textContext.setTextFont(dfont.font);
                        textContext.setTextSize(dfont.size);
                        textContext.setAscendor(dfont.ascendor);
                        textContext.setInitialized(true);
                        beginTextMode(cb);
                        break;
                    }
                    case DrawChars: {
                        textMode(cb);
                        OpDrawChars opDrawChars = (OpDrawChars) op;
                        String chars = opDrawChars.getChars();
                        List<Double> xs = opDrawChars.getXs();
                        List<Double> ys = opDrawChars.getYs();
                        for (int j = 0; j < chars.length(); j++) {
                            String text = chars.substring(j, j + 1);
                            double x = j < xs.size() ? xs.get(j) : xs.get(xs.size() - 1);
                            double y = j < ys.size() ? ys.get(j) : ys.get(ys.size() - 1);
                            cb.setTextMatrix(getX(x), getY(y) - textContext.getAscendor());
                            cb.showText(text);
                        }
                        break;
                    }
                    case SetTextColor: {
                        OpSetTextColor opSetTextColor = (OpSetTextColor) op;
                        BaseColor baseColor = new BaseColor(
                                opSetTextColor.getR(),
                                opSetTextColor.getG(),
                                opSetTextColor.getB()
                        );
                        textMode(cb);
                        endTextMode(cb);
                        textContext.setTextColor(baseColor);
                        beginTextMode(cb);
                        break;
                    }
                    case CreatePen: {
                        OpCreatePen opCreatePen = (OpCreatePen) op;
                        String name = opCreatePen.getName();
                        if (!strokeMap.containsKey(name)) {
                            BaseColor color = new BaseColor(opCreatePen.getR(), opCreatePen.getG(),
                                    opCreatePen.getB());
                            float width = (float) milliToPoint(opCreatePen.getWidth());
                            float[] pstyle = null;
                            List<Double> penStyle = opCreatePen.getPenStyle();
                            if (penStyle.size() > 0) {
                                pstyle = new float[penStyle.size()];
                                for (int j = 0; j < penStyle.size(); j++) {
                                    pstyle[j] = (float) milliToPoint(penStyle.get(j));
                                }
                            }
                            StrokeData data = new StrokeData(color, width, pstyle);
                            strokeMap.put(name, data);
                        }
                        break;
                    }
                    case SetPen: {
                        OpSetPen opSetPen = (OpSetPen) op;
                        String name = opSetPen.getName();
                        StrokeData data = strokeMap.getOrDefault(name, null);
                        if (data == null) {
                            throw new RuntimeException("Cannot find pen: " + name);
                        }
                        graphicMode(cb);
                        endGraphicMode(cb);
                        graphicContext.setStrokeColor(data.color);
                        graphicContext.setStrokeWidth(data.width);
                        graphicContext.setStrokeStyle(data.style);
                        graphicContext.setInitialized(true);
                        beginGraphicMode(cb);
                        break;
                    }
                    case MoveTo: {
                        graphicMode(cb);
                        cb.stroke();
                        OpMoveTo opMoveTo = (OpMoveTo) op;
                        float x = getX(opMoveTo.getX());
                        float y = getY(opMoveTo.getY());
                        cb.moveTo(x, y);
                        break;
                    }
                    case LineTo: {
                        OpLineTo opLineTo = (OpLineTo) op;
                        float x = getX(opLineTo.getX());
                        float y = getY(opLineTo.getY());
                        cb.lineTo(x, y);
                        break;
                    }
                    case Circle: {
                        graphicMode(cb);
                        cb.stroke();
                        OpCircle opCircle = (OpCircle) op;
                        float x = getX(opCircle.getCx());
                        float y = getY(opCircle.getCy());
                        float r = (float) milliToPoint(opCircle.getR());
                        cb.arc(x - r, y + r, x + r, y - r, 0, 360);
                        cb.stroke();
                        break;
                    }
                    default:
                        logger.warn("Unknown op in PdfPrinter (ignored): " + op.toString());
                        break;
                }
            }
            if (inText) {
                endTextMode(cb);
                inText = false;
            } else {
                endGraphicMode(cb);
            }
            if (callback != null) {
                callback.proc(cb, pages.size(), () -> graphicMode(cb), () -> textMode(cb));
            }
        }
        doc.close();
    }

    public void print(List<List<Op>> pages, OutputStream outStream) throws Exception {
        print(pages, outStream, null);
    }

    public void print(List<List<Op>> pages, String savePath) throws Exception {
        OutputStream outStream;
        if ("-".equals(savePath)) {
            outStream = System.out;
        } else {
            outStream = new FileOutputStream(savePath);
        }
        print(pages, outStream);
    }

}

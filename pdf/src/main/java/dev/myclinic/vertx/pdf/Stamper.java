package dev.myclinic.vertx.pdf;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Stamper {

    public static class StamperOption {
        public double scale = 1.0;
        public double xPos = 0.0;   // in mm
        public double yPos = 0.0;   // in mm
        public boolean stampCenterRelative = true;
    }

    public void putStampAtPage(InputStream is, String imageFile, OutputStream os, StamperOption opt,
                               int page) {
        if (opt == null) {
            opt = new StamperOption();
        }
        try {
            PdfReader reader = new PdfReader(is);
            PdfStamper stamper = new PdfStamper(reader, os);
            Image image = Image.getInstance(imageFile);
            image.scalePercent((float) (opt.scale * 100));
            double x = Unit.mmToPoint(opt.xPos);
            double y = Unit.mmToPoint(opt.yPos);
            if (opt.stampCenterRelative) {
                x -= image.getScaledWidth() / 2.0;
                y -= image.getScaledHeight() / 2.0;
            }
            image.setAbsolutePosition((float) x, (float) y);
            PdfContentByte cb = stamper.getOverContent(page);
            cb.addImage(image);
            stamper.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putStampAtPage(String srcPdfFile, String imageFile, String outputFile, StamperOption opt,
                               int page) {
        try(InputStream is = new FileInputStream(srcPdfFile);
            OutputStream os = new FileOutputStream(outputFile)){
            putStampAtPage(is, imageFile, os, opt, page);
        } catch(Exception e){
            throw new RuntimeException(e);
        }

//        if (opt == null) {
//            opt = new StamperOption();
//        }
//        try {
//            InputStream is = new FileInputStream(srcPdfFile);
//            PdfReader reader = new PdfReader(is);
//            OutputStream os = new FileOutputStream(outputFile);
//            PdfStamper stamper = new PdfStamper(reader, os);
//            Image image = Image.getInstance(imageFile);
//            image.scalePercent((float) (opt.scale * 100));
//            double x = Unit.mmToPoint(opt.xPos);
//            double y = Unit.mmToPoint(opt.yPos);
//            if (opt.stampCenterRelative) {
//                x -= image.getScaledWidth() / 2.0;
//                y -= image.getScaledHeight() / 2.0;
//            }
//            image.setAbsolutePosition((float) x, (float) y);
//            PdfContentByte cb = stamper.getOverContent(page);
//            cb.addImage(image);
//            stamper.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    public void putStampAtLastPage(String srcPdfFile, String imageFile, String outputFile, StamperOption opt) {
        if (opt == null) {
            opt = new StamperOption();
        }
        try {
            InputStream is = new FileInputStream(srcPdfFile);
            PdfReader reader = new PdfReader(is);
            int nPages = reader.getNumberOfPages();
            OutputStream os = new FileOutputStream(outputFile);
            PdfStamper stamper = new PdfStamper(reader, os);
            Image image = Image.getInstance(imageFile);
            image.scalePercent((float) (opt.scale * 100));
            double x = Unit.mmToPoint(opt.xPos);
            double y = Unit.mmToPoint(opt.yPos);
            if (opt.stampCenterRelative) {
                x -= image.getScaledWidth() / 2.0;
                y -= image.getScaledHeight() / 2.0;
            }
            image.setAbsolutePosition((float) x, (float) y);
            PdfContentByte cb = stamper.getOverContent(nPages);
            cb.addImage(image);
            stamper.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putStamp(String srcPdfFile, String imageFile, String outputFile, StamperOption opt) {
        putStampAtLastPage(srcPdfFile, imageFile, outputFile, opt);
    }

}

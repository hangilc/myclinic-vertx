package dev.myclinic.vertx.pdfstamp;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class Main {

    public static void main(String[] args) throws Exception {
        CmdArgs cmdArgs = CmdArgs.parse(args);
        PdfReader reader = new PdfReader(cmdArgs.lowerPdf);
        PdfStamper stamper = new PdfStamper(reader, System.out);
        Image image = Image.getInstance(cmdArgs.stampImage);
        image.scalePercent((float) (cmdArgs.scale * 100));
        double x = mmToPoint(cmdArgs.xPos);
        x -= image.getScaledWidth() / 2.0;
        double y = mmToPoint(cmdArgs.yPos);
        y -= image.getScaledHeight() / 2.0;
        image.setAbsolutePosition((float) x, (float) y);
        PdfContentByte cb = stamper.getOverContent(1);
        cb.addImage(image);
        stamper.close();
    }

    public static double mmToPoint(double mmValue){
        return mmValue / 25.4 * 72.0;
    }

    public static double pointToMm(double pointValue){
        return pointValue / 72.0 * 25.4;
    }

}

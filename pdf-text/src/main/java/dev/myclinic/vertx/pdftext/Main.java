package dev.myclinic.vertx.pdftext;

import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.drawerform.Paper;
import dev.myclinic.vertx.drawerform.textform.TextData;
import dev.myclinic.vertx.drawerform.textform.TextForm;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if( args.length > 0 && args[0].equals("-h") ){
            usage();
            System.exit(1);
        }
        String src = readInput(System.in);
        TextData data = new TextData();
        data.text = src;
        Paper paper = Paper.A4;
        TextForm form = new TextForm();
        List<List<Op>> pages = form.render(data);
        PaperSize paperSize = new PaperSize(paper.getWidth(), paper.getHeight());
        PdfPrinter pdfPrinter = new PdfPrinter(paperSize);
        pdfPrinter.print(pages, System.out);
    }

    static String readInput(InputStream fin) throws IOException {
        byte[] bytes = fin.readAllBytes();
        return new String(bytes, "UTf-8");
    }

    static void usage(){
        System.err.println("Usage: pdf-text    (accepts input from stdin and outputs to stdout)");
    }

}

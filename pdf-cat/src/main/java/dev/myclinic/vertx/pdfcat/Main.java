package dev.myclinic.vertx.pdfcat;


import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        Document doc = new Document();
        PdfCopy copy = new PdfCopy(doc, System.out);
        doc.open();
        for(String fileName: args){
            InputStream fin = new FileInputStream(fileName);
            PdfReader reader = new PdfReader(fin);
            copy.addDocument(reader);
            copy.freeReader(reader);
            reader.close();
        }
        doc.close();
    }

    public static void main2(String[] args) throws Exception {
        Document doc = new Document();
        PdfWriter writer = PdfWriter.getInstance(doc, System.out);
        doc.open();
        PdfContentByte cb = writer.getDirectContent();
        for(String fileName: args){
            InputStream is = new FileInputStream(fileName);
            PdfReader reader = new PdfReader(is);
            int nPages = reader.getNumberOfPages();
            for(int i=1;i<=nPages;i++){
                doc.newPage();
                PdfImportedPage page = writer.getImportedPage(reader, i);
                cb.addTemplate(page, 0, 0);
            }
            is.close();
        }
        writer.close();
    }

}

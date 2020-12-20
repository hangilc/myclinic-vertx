package dev.myclinic.vertx.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.*;
import java.util.List;

public class Concatenator {

    private Concatenator() {

    }

    public static void concatenate(List<String> srcFiles, String outFile)
            throws DocumentException, IOException {
        OutputStream os = new FileOutputStream(outFile);
        Document doc = new Document();
        PdfCopy copy = new PdfCopy(doc, os);
        doc.open();
        for (String fileName : srcFiles) {
            InputStream fin = new FileInputStream(fileName);
            PdfReader reader = new PdfReader(fin);
            copy.addDocument(reader);
            copy.freeReader(reader);
            reader.close();
        }
        doc.close();
    }

}

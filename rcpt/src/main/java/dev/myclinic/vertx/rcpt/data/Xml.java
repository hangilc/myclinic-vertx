package dev.myclinic.vertx.rcpt.data;

import dev.myclinic.vertx.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

class Xml {

    private static Logger logger = LoggerFactory.getLogger(Xml.class);

    private String indentStr = "";
    private PrintStream out;

    Xml() {
        try {
            this.out = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to create PrintStream. {}", e);
            throw new RuntimeException(e);
        }
    }

    void prelude(){
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    void element(String tag, Runnable inner){
        out.printf("%s<%s>\n", indentStr, tag);
        indent();
        inner.run();
        unindent();
        out.printf("%s</%s>\n", indentStr, tag);
    }

    void element(String tag, int value){
        out.printf("%s<%s>%d</%s>\n", indentStr, tag, value, tag);
    }

    void element(String tag, String value){
        out.printf("%s<%s>%s</%s>\n", indentStr, tag, value, tag);
    }

    void element(String tag, char value){
        out.printf("%s<%s>%c</%s>\n", indentStr, tag, value, tag);
    }

    void element(String tag, double value){
        String rep = NumberUtil.formatNumber(value);
        out.printf("%s<%s>%s</%s>\n", indentStr, tag, rep, tag);
    }

    void element(String tag, String fmt, double value){
        out.printf("%s<%s>" + fmt + "</%s>\n", indentStr, tag, value, tag);
    }

    void indent(){
        indentStr += "  ";
    }

    void unindent(){
        if( indentStr.length() >= 2 ) {
            indentStr = indentStr.substring(2);
        }
    }

}

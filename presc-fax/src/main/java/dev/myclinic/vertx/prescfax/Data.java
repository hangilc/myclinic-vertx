package dev.myclinic.vertx.prescfax;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data {

    public static Data create(LocalDate from, LocalDate upto) throws IOException, InterruptedException {
        throw new RuntimeException("not implemented");
    }

    private final static Pattern prescPattern = Pattern.compile("^\\s*院外処方\\s*\\r?\\n");

    public static boolean isPrescContent(String content){
        Matcher m = prescPattern.matcher(content);
        return m.find();
    }

    private final static Pattern pharmaFaxPattern = Pattern.compile("^(.+)にファックス（(\\+\\d+)）");

    public static class PharmaFax {
        public String pharma;
        public String fax;

        public PharmaFax(String pharma, String fax) {
            this.pharma = pharma;
            this.fax = fax;
        }
    }

    public static PharmaFax isPharmaFax(String content){
        Matcher m = pharmaFaxPattern.matcher(content);
        if( m.find() ){
            return new PharmaFax(m.group(1), m.group(2));
        } else {
            return null;
        }
    }

    private final static Pattern prescNotFaxPattern = Pattern.compile(
            "処方箋を渡した|自宅に処方箋を郵送|(電子)?メールで処方箋を送付した|処方箋を自宅にファックスで送った");

    public static boolean isPrescNotFax(String content){
        Matcher m = prescNotFaxPattern.matcher(content);
        return m.find();
    }

}

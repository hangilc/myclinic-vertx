package dev.myclinic.vertx.cli.covidvaccine.logentry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneLog implements LogEntry {

    public int patientId;
    public String phone;

    public PhoneLog(int patientId, String phone) {
        this.patientId = patientId;
        this.phone = phone;
    }

    private static final Pattern pat = Pattern.compile("PHONE\\s+(\\d+)\\s+(.+)");

    public static PhoneLog tryParse(String src){
        Matcher m = pat.matcher(src);
        if( m.matches() ){
            int patientId = Integer.parseInt(m.group(1));
            String phone = m.group(2);
            return new PhoneLog(patientId, phone);
        } else {
            return null;
        }
    }
}

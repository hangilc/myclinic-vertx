package dev.myclinic.vertx.cli.covidvaccine.logentry;

import dev.myclinic.vertx.cli.covidvaccine.PatientState;
import dev.myclinic.vertx.cli.covidvaccine.patientevent.PatientEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StateLog implements LogEntry {

    public int patientId;
    public PatientEvent event;

    public StateLog(int patientId, PatientEvent event) {
        this.patientId = patientId;
        this.event = event;
    }

    public static class ParseResult {
        public int patientId;
        public String attr;

        public ParseResult(int patientId, String attr) {
            this.attr = attr;
            this.patientId = patientId;
        }
    }

    private static final Pattern pat = Pattern.compile("STATE\\s+(\\d+)\\s+(\\S+)");

    public static ParseResult tryParse(String src){
        Matcher m = pat.matcher(src);
        if( m.matches() ){
            int patientId = Integer.parseInt(m.group(1));
            String attr = m.group(2);
            return new ParseResult(patientId, attr);
        } else {
            return null;
        }
    }

}

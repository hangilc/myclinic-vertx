package dev.myclinic.vertx.cli.covidvaccine.logentry;

import dev.myclinic.vertx.cli.covidvaccine.Patient;
import dev.myclinic.vertx.cli.covidvaccine.logentry.LogEntry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPatientLog implements LogEntry {

    public Patient patient;

    AddPatientLog(Patient patient) {
        this.patient = patient;
    }

    private static final Pattern pat = Pattern.compile("ADD\\s+(\\d+)\\s+(\\S+)\\s+(\\d+)\\s+(.+)");

    public static AddPatientLog tryParse(String src){
        if( src.startsWith("ADD ") ){
            Matcher m = pat.matcher(src);
            if( m.matches() ){
                Patient p = new Patient(
                        Integer.parseInt(m.group(1)),
                        m.group(2),
                        Integer.parseInt(m.group(3)),
                        m.group(4)
                );
                return new AddPatientLog(p);
            } else {
                throw new RuntimeException("Invalid ADD log: " + src);
            }
        } else {
            return null;
        }
    }

}

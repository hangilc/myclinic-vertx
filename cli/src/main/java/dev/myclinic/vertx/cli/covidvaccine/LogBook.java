package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.covidvaccine.logentry.AddPatientLog;
import dev.myclinic.vertx.cli.covidvaccine.logentry.LogEntry;
import dev.myclinic.vertx.cli.covidvaccine.logentry.PhoneLog;
import dev.myclinic.vertx.cli.covidvaccine.logentry.StateLog;
import dev.myclinic.vertx.cli.covidvaccine.patientevent.FirstShotAppoint;
import dev.myclinic.vertx.cli.covidvaccine.patientevent.PatientEvent;

import java.util.function.Consumer;
import java.util.regex.Matcher;

public class LogBook {

    public static void parseLog(String src, Consumer<LogEntry> handler) {
        LogEntry entry = AddPatientLog.tryParse(src);
        if (entry != null) {
            handler.accept(entry);
            return;
        }
        StateLog.ParseResult stateParseResult = StateLog.tryParse(src);
        if (stateParseResult != null) {
            int patientId = stateParseResult.patientId;
            String attr = stateParseResult.attr;
            Matcher m = FirstShotAppoint.oldPattern.matcher(attr);
            if (m.matches()) {
                String attr1 = m.group(1);
                String attr2 = m.group(2);
                PatientEvent event1 = CovidMisc.parsePatientAttr(attr1);
                handler.accept(new StateLog(patientId, event1));
                PatientEvent event2 = CovidMisc.parsePatientAttr(attr2);
                handler.accept(new StateLog(patientId, event2));
            } else {
                PatientEvent event = CovidMisc.parsePatientAttr(attr);
                handler.accept(new StateLog(patientId, event));
            }
            return;
        }
        entry = PhoneLog.tryParse(src);
        if (entry != null) {
            handler.accept(entry);
            return;
        }
        throw new RuntimeException("Cannot handle log: " + src);
    }

}

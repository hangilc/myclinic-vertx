package dev.myclinic.vertx.cli.covidvaccine;

import java.util.List;

public class Context {

    private List<String> logs;
    private List<AppointDate> appointDates;
    private AppointCalendar cal;

    private void ensureLogs(){
        if( logs == null ){
            logs = CovidVaccine.readLogs();
        }
    }

    private void ensureAppointDates(){
        if( appointDates == null ){
            appointDates = CovidVaccine.readAppointDates();
        }
    }

    private void ensureCalendar(){
        if( cal == null ){
            ensureLogs();
            ensureAppointDates();
            cal = new AppointCalendar();
            cal.init(logs, appointDates);
        }
    }

    public List<String> getLogs(){
        ensureLogs();
        return logs;
    }

    public AppointCalendar getCalendar(){
        ensureCalendar();
        return cal;
    }

}

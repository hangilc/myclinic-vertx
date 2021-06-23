package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.cli.covidvaccine.appointslot.FirstShotSlot;
import dev.myclinic.vertx.cli.covidvaccine.appointslot.SecondShotSlot;
import dev.myclinic.vertx.cli.covidvaccine.logentry.AddPatientLog;
import dev.myclinic.vertx.cli.covidvaccine.logentry.LogEntry;
import dev.myclinic.vertx.cli.covidvaccine.logentry.PhoneLog;
import dev.myclinic.vertx.cli.covidvaccine.logentry.StateLog;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

class AppointBook {

    private final Path baseDir;
    private List<LogEntry> logs;
    private Map<Integer, Patient> patientMap;
    private Map<LocalDateTime, AppointDate> appointDateMap;
    private Map<Integer, PatientState> patientStateMap;
    private Map<LocalDateTime, AppointBlock> blockMap;

    AppointBook(String baseDir){
        this.baseDir = Path.of(baseDir);
    }

    private void ensureLogs(){
        if( this.logs == null ){
            this.logs = new ArrayList<>();
            Misc.forEachLine(baseDir.resolve("logbook.txt"), line -> {
                line = line.trim();
                LogBook.parseLog(line, entry -> logs.add(entry));
            });
        }
    }

    private void ensurePatientMap(){
        if( this.patientMap == null ) {
            ensureLogs();
            this.patientMap = new HashMap<>();
            this.logs.forEach(log -> {
                if (log instanceof AddPatientLog) {
                    Patient p = ((AddPatientLog) log).patient;
                    patientMap.put(p.patientId, p);
                } else if( log instanceof PhoneLog){
                    PhoneLog phoneLog = (PhoneLog) log;
                    Patient patient = patientMap.get(phoneLog.patientId);
                    patient.phone = phoneLog.phone;
                }
            });
        }
    }

    private void ensureAppointDateMap(){
        if( appointDateMap == null ){
            this.appointDateMap = new LinkedHashMap<>();
            Misc.forEachLine(baseDir.resolve("appoint-dates.txt"), line -> {
                if( !line.isBlank() ){
                    AppointDate appointDate = AppointDate.parse(line);
                    appointDateMap.put(appointDate.at, appointDate);
                }
            });
        }
    }

    private void ensurePatientStateMap(){
        if( patientStateMap == null ){
            this.patientStateMap = new HashMap<>();
            ensureLogs();
            logs.forEach(entry -> {
                if( entry instanceof StateLog){
                    StateLog stateLog = (StateLog) entry;
                    PatientState patientState = patientStateMap.computeIfAbsent(stateLog.patientId,
                            k -> new PatientState());
                    patientState.apply(stateLog.event);
                }
            });
        }
    }

    private void ensureBlockMap(){
        if( this.blockMap == null ){
            this.blockMap = new LinkedHashMap<>();
            ensureAppointDateMap();
            appointDateMap.keySet().forEach(at -> {
                AppointDate appointDate = appointDateMap.get(at);
                blockMap.put(at, new AppointBlock(appointDate));
            });
            ensurePatientStateMap();
            for(int patientId: patientStateMap.keySet()){
                PatientState ps = patientStateMap.get(patientId);
                if (ps.firstShotTime != null && ps.firstShotState != FirstShotState.External) {
                    FirstShotSlot slot = new FirstShotSlot(patientId, ps.firstShotState);
                    AppointBlock block = blockMap.get(ps.firstShotTime);
                    block.addSlot(slot);
                }
                if (ps.secondShotTime != null) {
                    SecondShotSlot slot = new SecondShotSlot(patientId, ps.secondShotState);
                    AppointBlock block = blockMap.get(ps.secondShotTime);
                    block.addSlot(slot);
                }
            }
            checkOverbooking();
        }
    }

    public void checkOverbooking(){
        for(AppointBlock block: blockMap.values()){
            if( block.isOverbooking() ){
                String msg = String.format("Overbooking at %s!", CovidMisc.encodeAppointTime(block.appointDate.at));
                throw new RuntimeException(msg);
            }
        }
    }

    public Patient getPatient(int patientId){
        ensurePatientMap();
        return patientMap.get(patientId);
    }

    public PatientState getPatientState(int patientId){
        ensurePatientStateMap();
        return patientStateMap.get(patientId);
    }

    public List<LocalDateTime> listAppointTime(){
        ensureAppointDateMap();
        return new ArrayList<>(appointDateMap.keySet());
    }

    public AppointDate getAppointDate(LocalDateTime at){
        ensureAppointDateMap();
        return appointDateMap.get(at);
    }

    public AppointBlock getAppointBlock(LocalDateTime at){
        ensureBlockMap();
        return blockMap.get(at);
    }

}

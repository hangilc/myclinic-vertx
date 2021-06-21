package dev.myclinic.vertx.cli.covidvaccine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static dev.myclinic.vertx.cli.covidvaccine.PatientState.*;

import static java.util.stream.Collectors.toList;

public class AppointCalendar {

    private static class Item {
        public int patientId;
        public PatientState patientState;

        public Item(int patientId, PatientState patientState) {
            this.patientId = patientId;
            this.patientState = patientState;
        }
    }

    public static class Entry {
        public AppointDate appointDate;
        public List<Item> items = new ArrayList<>();

        public Entry(AppointDate appointDate) {
            this.appointDate = appointDate;
        }

        public void checkOverbooking(){
            if( appointDate.capacity < items.size() ){
                throw new RuntimeException("Overbooking! " + appointDate.at);
            }
        }

        public void iter(BiConsumer<Integer, FirstShotState> firstHandler,
                         BiConsumer<Integer, SecondShotState> secondHandler){
            LocalDateTime at = appointDate.at;
            items.forEach(e -> {
                if( at.equals(e.patientState.firstShotTime) ){
                    firstHandler.accept(e.patientId, e.patientState.firstShotState);
                }
                if( at.equals(e.patientState.secondShotTime) ){
                    secondHandler.accept(e.patientId, e.patientState.secondShotState);
                }
            });
        }
    }

    private final Map<LocalDateTime, Entry> cal = new LinkedHashMap<>();

    public void init(){
        CovidVaccine.readAppointDates().forEach(app -> {
            cal.put(app.at, new Entry(app));
        });
    }

    public void add(int patientId, PatientState ps){
        if( ps.firstShotTime != null && ps.firstShotState != PatientState.FirstShotState.External ){
            Entry entry = cal.get(ps.firstShotTime);
            entry.items.add(new Item(patientId, ps));
            entry.checkOverbooking();
        }
        if( ps.secondShotTime != null ){
            Entry entry = cal.get(ps.secondShotTime);
            entry.items.add(new Item(patientId, ps));
            entry.checkOverbooking();
        }
    }

    public List<AppointDate> listAppointDates(){
        return cal.values().stream().map(e -> e.appointDate).collect(toList());
    }

    public void iterItem(LocalDateTime at, BiConsumer<Integer, FirstShotState> firstHandler,
                                       BiConsumer<Integer, SecondShotState> secondHandler){
        cal.get(at).iter(firstHandler, secondHandler);
    }

}

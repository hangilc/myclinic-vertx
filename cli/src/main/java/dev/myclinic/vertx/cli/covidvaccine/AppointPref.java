package dev.myclinic.vertx.cli.covidvaccine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AppointPref {

    public static AppointPref parse(String src){
        AppointPref ap = new AppointPref();
        for(String arg: src.split("\\s+") ){
            arg = arg.trim();
            Pref pref = ExcludeDates.parse(arg);
            if( pref != null ){
                ap.addPref(pref);
                continue;
            }
            pref = OnlyAtTimes.parse(arg);
            if( pref != null ){
                ap.addPref(pref);
                continue;
            }
            throw new RuntimeException("Invalid appoint pref: " + arg);
        }
        return ap;
    }

    interface Pref {
        boolean acceptable(LocalDateTime at);
    }

    private final List<Pref> prefs = new ArrayList<>();

    public void addPref(Pref pref){
        this.prefs.add(pref);
    }

    public boolean acceptable(LocalDateTime at){
        for(Pref pref: prefs){
            if( !pref.acceptable(at) ){
                return false;
            }
        }
        return true;
    }

    private static class ExcludeDates implements Pref {

        private static final Pattern pat = Pattern.compile("exclude-dates=(.+)");

        public static ExcludeDates parse(String src){
            Matcher m = pat.matcher(src);
            if( m.matches() ){
                ExcludeDates pref = new ExcludeDates();
                String[] items = m.group(1).split("\\s*,\\s*");
                for(String item: items){
                    item = item.trim();
                    if( item.matches("\\d+-\\d+") ){
                        item = String.format("%d-%s", LocalDate.now().getYear(), item);
                    }
                    LocalDate at = LocalDate.parse(item);
                    pref.excludedDates.add(at);
                }
                return pref;
            } else {
                return null;
            }
        }

        private final List<LocalDate> excludedDates = new ArrayList<>();

        @Override
        public boolean acceptable(LocalDateTime at) {
            LocalDate atDate = at.toLocalDate();
            for(LocalDate ex: excludedDates){
                if( ex.equals(atDate) ){
                    return false;
                }
            }
            return true;
        }
    }

    private static class OnlyAtTimes implements Pref {

        private final List<LocalTime> acceptableTimes = new ArrayList<>();

        @Override
        public boolean acceptable(LocalDateTime at) {
            for(LocalTime t: acceptableTimes){
                if( t.equals(at.toLocalTime())){
                    return true;
                }
            }
            return false;
        }

        private static final Pattern pat = Pattern.compile("only-at-times=(.+)");
        private static final Pattern patItem = Pattern.compile("(\\d+):(\\d+)");

        public static OnlyAtTimes parse(String src){
            Matcher m = pat.matcher(src);
            if( m.matches() ){
                OnlyAtTimes pref = new OnlyAtTimes();
                String[] items = m.group(1).split("\\s*,\\s*");
                for(String item: items){
                    item = item.trim();
                    Matcher mm = patItem.matcher(item);
                    if( mm.matches() ){
                        LocalTime lt = LocalTime.of(
                                Integer.parseInt(mm.group(1)),
                                Integer.parseInt(mm.group(2))
                        );
                        pref.acceptableTimes.add(lt);
                    } else {
                        throw new RuntimeException("Invalid appoint time: " + item);
                    }
                }
                return pref;
            } else {
                return null;
            }
        }
    }

}

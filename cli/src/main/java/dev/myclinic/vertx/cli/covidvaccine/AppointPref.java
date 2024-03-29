package dev.myclinic.vertx.cli.covidvaccine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AppointPref {

    public static AppointPref parse(String src) {
        AppointPref ap = new AppointPref();
        for(String opt: src.split("\\s*;\\s*") ){
            opt = opt.trim();
            List<Pref> prefOpt = new ArrayList<>();
            for (String arg : opt.split("\\s+")) {
                arg = arg.trim();
                Pref pref = ExcludeDates.parse(arg);
                if (pref != null) {
                    prefOpt.add(pref);
                    continue;
                }
                pref = OnlyAtTimes.parse(arg);
                if (pref != null) {
                    prefOpt.add(pref);
                    continue;
                }
                pref = Exclude.parse(arg);
                if (pref != null) {
                    prefOpt.add(pref);
                    continue;
                }
                pref = OnlyDayOfWeek.parse(arg);
                if (pref != null) {
                    prefOpt.add(pref);
                    continue;
                }
                pref = AfterDate.parse(arg);
                if (pref != null) {
                    prefOpt.add(pref);
                    continue;
                }
                pref = AcceptDates.parse(arg);
                if (pref != null) {
                    prefOpt.add(pref);
                    continue;
                }
                pref = ExcludeRange.parse(arg);
                if( pref != null ){
                    prefOpt.add(pref);
                    continue;
                }
                throw new RuntimeException("Invalid appoint pref: " + arg);
            }
            ap.addPrefOpt(prefOpt);
        }
        return ap;
    }

    interface Pref {
        boolean acceptable(LocalDateTime at);
    }

    private final List<List<Pref>> prefOpts = new ArrayList<>();

    public void addPrefOpt(List<Pref> prefOpt) {
        this.prefOpts.add(prefOpt);
    }

    private static boolean optAcceptable(List<Pref> opt, LocalDateTime at){
        for (Pref pref : opt) {
            if (!pref.acceptable(at)) {
                return false;
            }
        }
        return true;
    }

    public boolean acceptable(LocalDateTime at) {
        for(List<Pref> opt: prefOpts) {
            if( optAcceptable(opt, at) ){
                return true;
            }
        }
        return false;
    }

    private static class ExcludeDates implements Pref {

        private static final Pattern pat = Pattern.compile("exclude-dates=(.+)");

        public static ExcludeDates parse(String src) {
            Matcher m = pat.matcher(src);
            if (m.matches()) {
                ExcludeDates pref = new ExcludeDates();
                String[] items = m.group(1).split("\\s*,\\s*");
                for (String item : items) {
                    item = item.trim();
                    if (item.matches("\\d+-\\d+")) {
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
            for (LocalDate ex : excludedDates) {
                if (ex.equals(atDate)) {
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
            for (LocalTime t : acceptableTimes) {
                if (t.equals(at.toLocalTime())) {
                    return true;
                }
            }
            return false;
        }

        private static final Pattern pat = Pattern.compile("only-at-times=(.+)");
        private static final Pattern patItem = Pattern.compile("(\\d+):(\\d+)");

        public static OnlyAtTimes parse(String src) {
            Matcher m = pat.matcher(src);
            if (m.matches()) {
                OnlyAtTimes pref = new OnlyAtTimes();
                String[] items = m.group(1).split("\\s*,\\s*");
                for (String item : items) {
                    item = item.trim();
                    Matcher mm = patItem.matcher(item);
                    if (mm.matches()) {
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

    private static class Exclude implements Pref {

        private final List<LocalDateTime> excluded = new ArrayList<>();

        @Override
        public boolean acceptable(LocalDateTime at) {
            for (LocalDateTime t : excluded) {
                if (t.equals(at)) {
                    return false;
                }
            }
            return true;
        }

        private static final Pattern pat = Pattern.compile("exclude=(.+)");
        private static final Pattern patItem = Pattern.compile("(\\d+)-(\\d+)T(\\d+):(\\d+)");

        public static Exclude parse(String src) {
            Matcher m = pat.matcher(src);
            if (m.matches()) {
                Exclude pref = new Exclude();
                String[] items = m.group(1).split("\\s*,\\s*");
                for (String item : items) {
                    item = item.trim();
                    Matcher mm = patItem.matcher(item);
                    if (mm.matches()) {
                        LocalDate date = LocalDate.of(
                                LocalDate.now().getYear(),
                                Integer.parseInt(mm.group(1)),
                                Integer.parseInt(mm.group(2))
                        );
                        LocalDateTime at = LocalDateTime.of(
                                date,
                                LocalTime.of(
                                        Integer.parseInt(mm.group(3)),
                                        Integer.parseInt(mm.group(4)))
                        );
                        pref.excluded.add(at);
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

    private static class OnlyDayOfWeek implements Pref {

        private final List<DayOfWeek> acceptables = new ArrayList<>();

        @Override
        public boolean acceptable(LocalDateTime at) {
            DayOfWeek dow = at.getDayOfWeek();
            for(DayOfWeek accept: acceptables){
                if( accept.equals(dow) ){
                    return true;
                }
            }
            return false;
        }
        private static final Pattern pat = Pattern.compile("only-day-of-week=(.+)");
        private static final Pattern patItem = Pattern.compile("Sun|Mon|Tue|Wed|Thu|Fri|Sat");

        private static final Map<String, DayOfWeek> map = new HashMap<>();
        static {
            map.put("Sun", DayOfWeek.SUNDAY);
            map.put("Mon", DayOfWeek.MONDAY);
            map.put("Tue", DayOfWeek.TUESDAY);
            map.put("Wed", DayOfWeek.WEDNESDAY);
            map.put("Thu", DayOfWeek.THURSDAY);
            map.put("Fri", DayOfWeek.FRIDAY);
            map.put("Sat", DayOfWeek.SATURDAY);
        }

        public static OnlyDayOfWeek parse(String src) {
            Matcher m = pat.matcher(src);
            if (m.matches()) {
                OnlyDayOfWeek pref = new OnlyDayOfWeek();
                String[] items = m.group(1).split("\\s*,\\s*");
                for (String item : items) {
                    item = item.trim();
                    Matcher mm = patItem.matcher(item);
                    if (mm.matches()) {
                        DayOfWeek dow = map.get(item);
                        if( dow == null ){
                            throw new RuntimeException("Invalid day of week: " + item);
                        }
                        pref.acceptables.add(dow);
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

    private static class AfterDate implements Pref {

        private LocalDate date;

        @Override
        public boolean acceptable(LocalDateTime at) {
            return at.toLocalDate().equals(date) || at.toLocalDate().isAfter(date);
        }
        private static final Pattern pat = Pattern.compile("after-date=(\\d+)-(\\d+)");

        public static AfterDate parse(String src) {
            Matcher m = pat.matcher(src);
            if (m.matches()) {
                AfterDate pref = new AfterDate();
                pref.date = LocalDate.of(
                        LocalDate.now().getYear(),
                        Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2))
                );
                return pref;
            } else {
                return null;
            }
        }
    }

    private static class AcceptDates implements Pref {

        private final List<LocalDate> acceptables = new ArrayList<>();

        @Override
        public boolean acceptable(LocalDateTime at) {
            for(LocalDate d: acceptables){
                if( d.equals(at.toLocalDate()) ){
                    return true;
                }
            }
            return false;
        }

        private static final Pattern pat = Pattern.compile("accept-dates=(.+)");

        public static AcceptDates parse(String src) {
            Matcher m = pat.matcher(src);
            if (m.matches()) {
                AcceptDates pref = new AcceptDates();
                String[] items = m.group(1).split("\\s*,\\s*");
                for (String item : items) {
                    item = item.trim();
                    if (item.matches("\\d+-\\d+")) {
                        item = String.format("%d-%s", LocalDate.now().getYear(), item);
                    }
                    LocalDate at = LocalDate.parse(item);
                    pref.acceptables.add(at);
                }
                return pref;
            } else {
                return null;
            }
        }
    }

    private static class ExcludeRange implements Pref {
        private LocalDate from;
        private LocalDate upto;

        ExcludeRange(LocalDate from, LocalDate upto) {
            this.from = from;
            this.upto = upto;
        }

        private static final Pattern pat = Pattern.compile("exclude-range=(\\d+)-(\\d+)~(\\d+)-(\\d+)");

        public static ExcludeRange parse(String src){
            Matcher m = pat.matcher(src);
            if( m.matches() ){
                LocalDate from = LocalDate.of(
                        LocalDate.now().getYear(),
                        Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)));
                LocalDate upto = LocalDate.of(
                        LocalDate.now().getYear(),
                        Integer.parseInt(m.group(3)),
                        Integer.parseInt(m.group(4)));
                return new ExcludeRange(from, upto);
            } else {
                return null;
            }
        }

        @Override
        public boolean acceptable(LocalDateTime at) {
            LocalDate d = at.toLocalDate();
            if( (d.equals(from) || d.isAfter(from)) && (d.equals(upto) || d.isBefore(upto)) ){
                return false;
            } else {
                return true;
            }
        }
    }

}

package dev.myclinic.vertx.util.kanjidate;

import java.time.LocalDate;

public class KanjiDate {

    public static int gengouToYear(Gengou g, int nen){
        return g.getStartDate().getYear() + nen - 1;
    }

    public static GengouNenPair yearToGengou(int year, int month, int day){
        LocalDate d = LocalDate.of(year, month, day);
        for(Gengou g: Gengou.valuesRecentFirst()){
            if( !d.isBefore(g.getStartDate()) ){
                int nen = year - g.getStartDate().getYear() + 1;
                return new GengouNenPair(g, nen);
            }
        }
        throw new IllegalArgumentException("Cannot convert to gengou.");
    }

    public static GengouNenPair yearToGengou(LocalDate date){
        return yearToGengou(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static String toKanji(LocalDate date){
        return new KanjiDateRepBuilder(date).format1().build();
    }

}

package dev.myclinic.vertx.util.kanjidate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public enum Gengou {

    Meiji(LocalDate.of(1873, 1, 1), "明治", "Meiji"),
    Taishou(LocalDate.of(1912, 7, 30), "大正", "Taishou"),
    Shouwa(LocalDate.of(1926, 12, 25), "昭和", "Shouwa"),
    Heisei(LocalDate.of(1989, 1, 8), "平成", "Heisei"),
    Reiwa(LocalDate.of(2019, 5, 1), "令和", "Reiwa");

    private LocalDate startDate;
    private String kanjiRep;
    private String alphaRep;

    Gengou(LocalDate startDate, String kanjiRep, String alphaRep){
        this.startDate = startDate;
        this.kanjiRep = kanjiRep;
        this.alphaRep = alphaRep;
    }

    public LocalDate getStartDate(){
        return startDate;
    }

    public String getKanjiRep() {
        return kanjiRep;
    }

    public String getAlphaRep() {
        return alphaRep;
    }

    public static Gengou[] valuesRecentFirst(){
        Gengou[] values = values();
        int n = values.length / 2;
        for(int i=0;i<n;i++){
            Gengou tmp = values[i];
            values[i] = values[values.length - 1 - i];
            values[values.length - 1 - i] = tmp;
        }
        return values;
    }

    public static Gengou fromKanjiRep(String rep){
        for(Gengou g: values()){
            if( g.getKanjiRep().equals(rep) ){
                return g;
            }
        }
        return null;
    }

    public static Gengou Current = Heisei;

    public static List<Gengou> Recent = new ArrayList<>();
    static {
        Recent.add(Reiwa);
        Recent.add(Heisei);
    }

    public static Gengou MostRecent = Reiwa;

}

package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.Naifuku;

import java.util.ArrayList;
import java.util.List;

class NaifukuCollector {

    //private static Logger logger = LoggerFactory.getLogger(NaifukuCollector.class);
    private NaifukuRep naifukuRep;
    private int days;
    private List<Naifuku> naifukuList = new ArrayList<>();

    static List<NaifukuCollector> fromNaifukuList(List<Naifuku> naifukuList){
        List<NaifukuCollector> collectors = new ArrayList<>();
        for(Naifuku naifuku: naifukuList){
            boolean done = false;
            for(NaifukuCollector c: collectors){
                if( c.canAdd(naifuku) ){
                    c.add(naifuku);
                    done = true;
                    break;
                }
            }
            if( !done ){
                collectors.add(new NaifukuCollector(naifuku));
            }
        }
        return collectors;
    }

    NaifukuCollector(Naifuku drug) {
        this.naifukuRep = new NaifukuRep(drug);
        this.days = drug.days;
        naifukuList.add(drug);
    }

    boolean canAdd(Naifuku drug){
        return days == drug.days && naifukuRep.canAdd(drug);
    }

    void add(Naifuku drug){
        naifukuRep.add(drug);
        naifukuList.add(drug);
    }

    NaifukuRep getNaifukuRep() {
        return naifukuRep;
    }

    public int getDays() {
        return days;
    }

    public int getTanka(){
        return naifukuRep.getTanka();
    }

    public List<Naifuku> getNaifukuList() {
        return naifukuList;
    }
}

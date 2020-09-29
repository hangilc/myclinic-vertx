package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.output.Output;

import java.util.LinkedHashSet;
import java.util.Set;

public class Shuukei {

    //private static Logger logger = LoggerFactory.getLogger(Shuukei.class);
    private String prefix;
    private Set<Integer> tankaSet = new LinkedHashSet<>();
    private int count;
    private int ten;
    private boolean printTanka = true;
    private boolean printCount = true;

    public Shuukei(String prefix, boolean printTanka, boolean printCount) {
        this.prefix = prefix;
        this.printTanka = printTanka;
        this.printCount = printCount;
    }

    public void add(int tanka){
        tankaSet.add(tanka);
        count += 1;
        ten += tanka;
    }

    public void addWithoutCount(int tanka){
        tankaSet.add(tanka);
        this.ten += tanka;
    }

    public void set(Integer tanka, Integer count, Integer ten){
        this.tankaSet = new LinkedHashSet<>();
        tankaSet.add(tanka);
        this.count = count;
        this.ten = ten;
    }

    public void print(Output output){
        if( ten == 0 ){
            return;
        }
        Integer tankaValue = null;
        if( printTanka && tankaSet.size() == 1 ){
            tankaValue = tankaSet.toArray(new Integer[]{})[0];
        }
        Integer countValue = null;
        if( printCount ){
            countValue = count;
        }
        output.printShuukei(prefix, tankaValue, countValue, ten);
    }

}

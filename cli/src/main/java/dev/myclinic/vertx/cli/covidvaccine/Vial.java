package dev.myclinic.vertx.cli.covidvaccine;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vial {
    public LocalDate arrivedAt;
    public int amount;
    public LocalDate validFrom;
    public LocalDate validUpto;

    public Vial(LocalDate arrivedAt, int amount){
        this.arrivedAt = arrivedAt;
        this.amount = amount;
        this.validFrom = arrivedAt.plus(1, ChronoUnit.DAYS);
        this.validUpto = arrivedAt.plus(30, ChronoUnit.DAYS);
    }

    public int amountAt(LocalDate at){
        if( at.isBefore(validFrom) || at.isAfter(validUpto) ){
            return 0;
        } else {
            return amount;
        }
    }

    private static final Pattern pattern = Pattern.compile("(\\d+-\\d+-\\d+)\\s+(\\d+).*");

    public static Vial parse(String str){
        Matcher m = pattern.matcher(str);
        if( m.matches() ){
            LocalDate arrivedAt = LocalDate.parse(m.group(1));
            int amount = Integer.parseInt(m.group(2));
            return new Vial(arrivedAt, amount);
        } else {
            throw new RuntimeException("Cannot convert to Vial: " + str);
        }
    }

    public static int availableAt(List<Vial> vials, LocalDate at){
        int amount = 0;
        for(Vial vial: vials){
            amount += vial.amountAt(at);
        }
        return amount;
    }

    public static void consume(List<Vial> vials, LocalDate at, int amount){
        for(Vial vial: vials){
            if( at.isBefore(vial.validFrom) || at.isAfter(vial.validUpto) ){
                continue;
            }
            if( vial.amount > 0 ){
                vial.amount -= 1;
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "Vial{" +
                "arrivedAt=" + arrivedAt +
                ", amount=" + amount +
                ", validFrom=" + validFrom +
                ", validUpto=" + validUpto +
                '}';
    }
}

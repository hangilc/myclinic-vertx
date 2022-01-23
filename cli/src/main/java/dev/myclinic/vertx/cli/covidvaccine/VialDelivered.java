package dev.myclinic.vertx.cli.covidvaccine;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VialDelivered {
    public LocalDate arrivedAt;
    public int amount;
    public LocalDate validFrom;
    public LocalDate validUpto;

    public VialDelivered(LocalDate arrivedAt, int amount){
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

    public static VialDelivered parse(String str){
        Matcher m = pattern.matcher(str);
        if( m.matches() ){
            LocalDate arrivedAt = LocalDate.parse(m.group(1));
            int amount = Integer.parseInt(m.group(2));
            return new VialDelivered(arrivedAt, amount);
        } else {
            throw new RuntimeException("Cannot convert to Vial: " + str);
        }
    }

    public static int availableAt(List<VialDelivered> vialDelivereds, LocalDate at){
        int amount = 0;
        for(VialDelivered vialDelivered : vialDelivereds){
            amount += vialDelivered.amountAt(at);
        }
        return amount;
    }

    public static void consume(List<VialDelivered> vialDelivereds, LocalDate at, int amount){
        for(VialDelivered vialDelivered : vialDelivereds){
            if( at.isBefore(vialDelivered.validFrom) || at.isAfter(vialDelivered.validUpto) ){
                continue;
            }
            if( vialDelivered.amount > 0 ){
                vialDelivered.amount -= 1;
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

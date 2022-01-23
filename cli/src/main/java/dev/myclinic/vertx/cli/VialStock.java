package dev.myclinic.vertx.cli;

import dev.myclinic.vertx.cli.covidvaccine.VialDelivered;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class VialStock {

    private final List<VialDelivered> vials = new ArrayList<>();

    public void addVial(VialDelivered delivered){
        this.vials.add(delivered);
    }

    public boolean consume(LocalDate at){
        for(VialDelivered delivered: vials){
            if( at.isBefore(delivered.validFrom) || at.isAfter(delivered.validUpto) ){
                continue;
            }
            if( delivered.amount > 0 ){
                delivered.amount -= 1;
                return true;
            }
        }
        return false;
    }

    public int availableAt(LocalDate at){
        int avail = 0;
        for(VialDelivered delivered: vials){
            if( at.isBefore(delivered.validFrom) || at.isAfter(delivered.validUpto) ){
                continue;
            }
            avail += delivered.amount;
        }
        return avail;
    }

}

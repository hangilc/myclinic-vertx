package dev.myclinic.vertx.prescfax;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
//        String pharmaListPath = System.getenv("MYCLINIC_PHARMACY_LIST");
//        List<Pharmacy> pharmacyList = Pharmacy.readFromFile(pharmaListPath);
//        for(var p: pharmacyList){
//            System.out.println(p);
//        }
        Data.create(LocalDate.parse("2020-10-01"), LocalDate.parse("2020-10-15"));
        System.exit(0);
    }

}

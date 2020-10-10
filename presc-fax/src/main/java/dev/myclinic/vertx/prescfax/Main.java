package dev.myclinic.vertx.prescfax;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String pharmaListPath = System.getenv("MYCLINIC_PHARMACY_LIST");
        List<Pharmacy> pharmacyList = Pharmacy.readFromFile(pharmaListPath);
        for(var p: pharmacyList){
            System.out.println(p);
        }
    }

}

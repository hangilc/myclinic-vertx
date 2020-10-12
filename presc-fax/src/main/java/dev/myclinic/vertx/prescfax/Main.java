package dev.myclinic.vertx.prescfax;

import dev.myclinic.vertx.client2.Client;
import dev.myclinic.vertx.dto.TextDTO;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        Client client = new Client(System.getenv("MYCLINIC_SERVICE"));
        List<Presc> prescList = Presc.listPresc(client, LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 15));
        for(Presc presc: prescList){
            System.out.println(presc);
        }
    }

}

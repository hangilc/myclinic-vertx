package dev.myclinic.vertx.prescfax;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.client2.Client;
import dev.myclinic.vertx.dto.TextDTO;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        Client client = new Client(System.getenv("MYCLINIC_SERVICE"));
        Data data = Data.create(client, LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 15));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(System.out, data);
    }

}

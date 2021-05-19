package dev.myclinic.vertx.camelcomp.appoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.appoint.AppointPersist;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.sql.Connection;

public class CancelAppointProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        AppointDTO appoint = exchange.getIn().getBody(AppointDTO.class);
        Connection conn = exchange.getProperty("dbConnection", Connection.class);
        AppointPersist.cancelAppoint(conn, appoint);
    }

}

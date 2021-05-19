package dev.myclinic.vertx.camelcomp.appoint;

import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.appoint.AppointPersist;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.sql.Connection;

public class CancelAppointProcessor implements Processor {

    private final Connection conn;

    public CancelAppointProcessor(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        AppointDTO appoint = exchange.getIn().getBody(AppointDTO.class);
        AppointPersist.cancelAppoint(conn, appoint);
    }

}

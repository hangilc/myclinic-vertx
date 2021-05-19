package dev.myclinic.vertx.camelcomp.appoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.appoint.AppointPersist;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.sql.Connection;

public class CancelAppointLogProcessor implements Processor {

    private final Connection conn;
    private final ObjectMapper mapper;

    public CancelAppointLogProcessor(Connection conn, ObjectMapper mapper) {
        this.conn = conn;
        this.mapper = mapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        AppointDTO created = exchange.getIn().getBody(AppointDTO.class);
        AppointPersist.logAppointCanceled(conn, mapper, created);
    }
}

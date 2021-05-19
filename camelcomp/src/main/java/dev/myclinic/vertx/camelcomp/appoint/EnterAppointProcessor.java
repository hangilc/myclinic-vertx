package dev.myclinic.vertx.camelcomp.appoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.appoint.AppointPersist;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.sql.Connection;

public class EnterAppointProcessor implements Processor {

    private final Connection conn;
    private final ObjectMapper mapper;

    public EnterAppointProcessor(Connection conn, ObjectMapper mapper) {
        this.conn = conn;
        this.mapper = mapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg = exchange.getIn();
        Object obj = msg.getBody();
        AppointDTO appoint = (AppointDTO) obj;
        AppointPersist.enterAppoint(conn, mapper, appoint);
    }

}

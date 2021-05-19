package dev.myclinic.vertx.camelcli;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.camelcomp.appoint.CancelAppointLogProcessor;
import dev.myclinic.vertx.camelcomp.appoint.CancelAppointProcessor;
import dev.myclinic.vertx.camelcomp.appoint.EnterAppointLogProcessor;
import dev.myclinic.vertx.camelcomp.appoint.EnterAppointProcessor;
import dev.myclinic.vertx.db.MysqlDataSourceConfig;
import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appoint {

    public static void main(String[] args) throws Exception {
        DataSource ds = MysqlDataSourceFactory.create(new MysqlDataSourceConfig());
        ObjectMapper mapper = new ObjectMapper();
        //enter(ds, mapper);
        cancel(ds, mapper);
    }

    private static void cancel(DataSource ds, ObjectMapper mapper) throws Exception {
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder(){
            @Override
            public void configure() throws Exception {
                from("direct:start-cancel-appoint")
                        .process(new CancelAppointProcessor())
                        .process(new CancelAppointLogProcessor());
            }
        });
        context.start();
        AppointDTO appoint = new AppointDTO();
        appoint.appointDate = LocalDate.of(2021, 6, 1);
        appoint.appointTime = LocalTime.of(10, 0);
        appoint.patientName = "診療太郎";
        ProducerTemplate tmpl = context.createProducerTemplate();
        Exchange reply = tmpl.request("direct:start-cancel-appoint", ex -> {
            ex.setProperty("dbConnection", conn);
            ex.setProperty("objectMapper", mapper);
            ex.getIn().setBody(appoint);
        });
        if( reply.getException() != null ) {
            reply.getException().printStackTrace();
            conn.rollback();
        } else {
            conn.commit();
        }
        conn.close();
        context.stop();
    }

    private static void enter(DataSource ds, ObjectMapper mapper) throws Exception {
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder(){
            @Override
            public void configure() throws Exception {
                from("direct:start-enter-appoint")
                        .process(new EnterAppointProcessor())
                        .process(new EnterAppointLogProcessor());
            }
        });
        context.start();
        AppointDTO appoint = new AppointDTO();
        appoint.appointDate = LocalDate.of(2021, 6, 1);
        appoint.appointTime = LocalTime.of(10, 0);
        appoint.patientName = "診療太郎";
        ProducerTemplate tmpl = context.createProducerTemplate();
        Exchange reply = tmpl.request("direct:start-enter-appoint", ex -> {
            ex.setProperty("dbConnection", conn);
            ex.setProperty("objectMapper", mapper);
            ex.getIn().setBody(appoint);
        });
        if( reply.getException() != null ) {
            reply.getException().printStackTrace();
            conn.rollback();
        } else {
            conn.commit();
        }
        conn.close();
        context.stop();
    }

}

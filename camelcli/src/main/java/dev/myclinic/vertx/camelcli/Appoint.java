package dev.myclinic.vertx.camelcli;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.camelcomp.appoint.EnterAppointProcessor;
import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.db.MysqlDataSourceConfig;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

public class Appoint {

    public static void main(String[] args) throws Exception {
        DataSource ds = MysqlDataSourceFactory.create(new MysqlDataSourceConfig());
        ObjectMapper mapper = new ObjectMapper();
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        CamelContext context = new DefaultCamelContext();
        CompletableFuture<Void> done = new CompletableFuture<>();
        context.addRoutes(new RouteBuilder(){
            @Override
            public void configure() throws Exception {
                from("direct:start-enter-appoint")
                        .process(ex -> {
                            System.out.println("enter");
                        })
                        .process(new EnterAppointProcessor(conn, mapper))
                        .process(ex -> {
                            conn.commit();
                            conn.close();
                        })
                        .process(ex -> done.complete(null));
            }
        });
        context.start();
        AppointDTO appoint = new AppointDTO();
        appoint.appointDate = LocalDate.of(2021, 6, 1);
        appoint.appointTime = LocalTime.of(10, 0);
        appoint.patientName = "診療太郎";
        ProducerTemplate tmpl = context.createProducerTemplate();
        tmpl.sendBody("direct:start-enter-appoint", appoint);
        done.join();
        context.stop();
    }

}

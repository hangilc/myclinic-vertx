package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.appoint.AppointAPI;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.jackson.time.TimeModule;
import dev.myclinic.vertx.util.DateTimeUtil;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static dev.myclinic.vertx.server.RestHandler.CallResult;
import static dev.myclinic.vertx.server.RestHandler.RestFunction;

public class AppointRestHandler implements Handler<RoutingContext> {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new TimeModule());
    }

    @Override
    public void handle(RoutingContext ctx) {
        String action = ctx.request().getParam("action");
        try {
            switch (action) {
                case "list-appoint-time": {
                    listAppointTime(ctx);
                    break;
                }
                case "put-appoint": {
                    putAppoint(ctx);
                    break;
                }
                default: {
                    ctx.fail(new RuntimeException("Unknown action (appoint): " + action));
                }
            }
        } catch(Exception e){
            ctx.fail(e);
        }
    }

    private Connection openConnection(){
        String dbFile = System.getenv("MYCLINIC_APPOINT_DB");
        if( dbFile == null ){
            throw new RuntimeException("Cannot find env var: MYCLINIC_APPOINT_DB");
        }
        String url = String.format("jdbc:sqlite:%s", dbFile);
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private interface SqlProc {
        void execute(Connection conn) throws Exception;
    }

    private void withConnection(SqlProc consumer) {
        Connection conn = openConnection();
        try {
            conn.setAutoCommit(false);
            consumer.execute(conn);
            conn.commit();
        } catch(Exception ex){
            try {
                if( !conn.getAutoCommit() ) {
                    conn.rollback();
                }
                throw new RuntimeException(ex);
            } catch (SQLException th) {
                throw new RuntimeException(th);
            }
        } finally {
            try {
                conn.close();
            } catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

    private void listAppointTime(RoutingContext ctx){
        String fromParam = ctx.request().getParam("from");
        String uptoParam = ctx.request().getParam("upto");
        LocalDate from = DateTimeUtil.parseSqlDate(fromParam);
        LocalDate upto = DateTimeUtil.parseSqlDate(uptoParam);
        withConnection(conn -> {
            var rest = AppointAPI.listAppointTime(conn, from, upto);
            String json = mapper.writeValueAsString(rest);
            ctx.response().headers().add("content-type", "application/json");
            ctx.response().end(json);
        });
    }

    private void putAppoint(RoutingContext ctx) throws IOException {
        AppointDTO app = mapper.readValue(ctx.getBody().getBytes(), AppointDTO.class);
        System.out.println(app);
        ctx.response().end("ok");
    }


}

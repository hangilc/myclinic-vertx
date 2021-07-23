package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointAPI;
import dev.myclinic.vertx.util.DateTimeUtil;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static dev.myclinic.vertx.server.RestHandler.CallResult;
import static dev.myclinic.vertx.server.RestHandler.RestFunction;

public class AppointRestHandler implements Handler<RoutingContext> {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {

    }

    @Override
    public void handle(RoutingContext ctx) {
        String action = ctx.request().getParam("action");
        switch(action){
            case "list-appoint-time": {
                listAppointTime(ctx);
                break;
            }
            default: {
                throw new RuntimeException("Unknown action (appoint): " + action);
            }
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


}

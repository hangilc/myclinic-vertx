package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.appoint.AppointPersist;
import dev.myclinic.vertx.util.DateTimeUtil;
import dev.myclinic.vertx.util.kanjidate.KanjiDate;
import io.vertx.ext.web.RoutingContext;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

import static dev.myclinic.vertx.server.RestHandler.CallResult;
import static dev.myclinic.vertx.server.RestHandler.RestFunction;

class AppointRestHandler {

    private static CallResult callResult = new CallResult(
            Collections.emptyList(),
            Collections.emptyList()
    );

    interface AppointHandler {
        void handle(Connection conn, ObjectMapper mapper, RoutingContext ctx) throws Exception;
    }

    private static RestFunction handler(ObjectMapper mapper, AppointHandler appointHandler){
        return (ctx, conn) -> {
            appointHandler.handle(conn, mapper, ctx);
            return callResult;
        };
    }

    public static void register(Map<String, RestFunction> funcMap, ObjectMapper mapper){
        funcMap.put("get-appoint", handler(mapper, AppointRestHandler::getAppoint));
    }

    private static void getAppoint(Connection conn, ObjectMapper mapper, RoutingContext ctx) throws Exception {
        String paraAppointDate = ctx.request().getParam("appoint-date");
        String paraAppointTime = ctx.request().getParam("appoint-time");
        LocalDate appointDate = DateTimeUtil.parseSqlDate(paraAppointDate);
        LocalTime appointTime = DateTimeUtil.parseSqlTime(paraAppointTime);
        AppointDTO result = AppointPersist.getAppoint(conn, mapper, appointDate, appointTime);
        ctx.response().end(mapper.writeValueAsString(result.toJsonObject()));
    }

}

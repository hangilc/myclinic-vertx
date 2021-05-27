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
import java.util.List;
import java.util.Map;

import static dev.myclinic.vertx.server.RestHandler.CallResult;
import static dev.myclinic.vertx.server.RestHandler.RestFunction;
import static java.util.stream.Collectors.toList;

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
        funcMap.put("list-appoint-in-range", handler(mapper, AppointRestHandler::listAppointInRange));
        funcMap.put("enter-appoint", handler(mapper, AppointRestHandler::enterAppoint));
    }

    private static void getAppoint(Connection conn, ObjectMapper mapper, RoutingContext ctx) throws Exception {
        String paraAppointDate = ctx.request().getParam("date");
        String paraAppointTime = ctx.request().getParam("time");
        LocalDate appointDate = DateTimeUtil.parseSqlDate(paraAppointDate);
        LocalTime appointTime = DateTimeUtil.parseSqlTime(paraAppointTime);
        AppointDTO result = AppointPersist.getAppoint(conn, mapper, appointDate, appointTime);
        ctx.response().end(mapper.writeValueAsString(result.toJsonObject()));
    }

    private static void listAppointInRange(Connection conn, ObjectMapper mapper, RoutingContext ctx) throws Exception {
        String paraFrom = ctx.request().getParam("from");
        String paraUpto = ctx.request().getParam("upto");
        LocalDate fromDate = DateTimeUtil.parseSqlDate(paraFrom);
        LocalDate uptoDate = DateTimeUtil.parseSqlDate(paraUpto);
        List<AppointDTO> appoints = AppointPersist.listAppoint(conn, mapper, fromDate, uptoDate);
        List<Map<String,Object>> jobjs = appoints.stream().map(AppointDTO::toJsonObject).collect(toList());
        String json = mapper.writeValueAsString(jobjs);
        ctx.response().end(json);
    }

    private static void enterAppoint(Connection conn, ObjectMapper mapper, RoutingContext ctx) throws Exception {
        String dateParam = ctx.request().getParam("date");
        String timeParam = ctx.request().getParam("time");
        String name = ctx.request().getParam("name");
        AppointDTO appoint = new AppointDTO();
        appoint.appointDate = DateTimeUtil.parseSqlDate(dateParam);
        appoint.appointTime = DateTimeUtil.parseSqlTime(timeParam);
        appoint.patientName = name;
        AppointPersist.enterAppoint(conn, mapper, appoint);
        ctx.response().end("true");
    }

}

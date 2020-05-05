package dev.myclinic.vertx.server;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import javax.sql.DataSource;

class RestHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        String action = req.getParam("action");
        if( action.equals("get-patient") ){
            try {
                MultiMap params = req.params();
                int patientId = Integer.parseInt(params.get("patient-id"));
                routingContext.response().end(String.valueOf(patientId));
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        } else {
            routingContext.response().end("hello " + action);
        }
    }

}

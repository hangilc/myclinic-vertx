package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.db.Backend;
import dev.myclinic.vertx.db.Query;
import dev.myclinic.vertx.db.TableSet;
import dev.myclinic.vertx.dto.PatientDTO;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;

class RestHandler implements Handler<RoutingContext> {

    private static Logger logger = LoggerFactory.getLogger(RestHandler.class);

    private DataSource ds;
    private TableSet ts;
    private ObjectMapper mapper;

    public RestHandler(DataSource ds, TableSet ts, ObjectMapper mapper){
        this.ds = ds;
        this.ts = ts;
        this.mapper = mapper;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        String action = req.getParam("action");
        if( action.equals("get-patient") ){
            Connection conn = null;
            try {
                MultiMap params = req.params();
                int patientId = Integer.parseInt(params.get("patient-id"));
                conn = ds.getConnection();
                Query query = new Query(conn);
                Backend backend = new Backend(ts, query);
                PatientDTO patient = backend.getPatient(patientId);
                HttpServerResponse resp = req.response();
                resp.putHeader("content-type", "application/json; charset=UTF-8");
                String result = mapper.writeValueAsString(patient);
                resp.end(result);
            } catch(Exception e){
                if( conn != null ){
                    try {
                        conn.rollback();
                    } catch(Exception ex){
                        logger.error("Rollback failed", ex);
                    }
                }
                throw new RuntimeException(e);
            }
        } else {
            routingContext.response().end("hello " + action);
        }
    }

}

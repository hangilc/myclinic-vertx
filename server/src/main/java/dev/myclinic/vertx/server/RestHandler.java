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
import java.util.HashMap;
import java.util.Map;

class RestHandler implements Handler<RoutingContext> {

    private static Logger logger = LoggerFactory.getLogger(RestHandler.class);

    private DataSource ds;
    private TableSet ts;
    private ObjectMapper mapper;

    interface RestFunction {
        void call(HttpServerRequest req, Connection conn) throws Exception;
    }

    public RestHandler(DataSource ds, TableSet ts, ObjectMapper mapper){
        this.ds = ds;
        this.ts = ts;
        this.mapper = mapper;
    }

    private void getPatient(HttpServerRequest req, Connection conn) throws Exception {
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        Query query = new Query(conn);
        Backend backend = new Backend(ts, query);
        PatientDTO patient = backend.getPatient(patientId);
        conn.commit();
        String result = mapper.writeValueAsString(patient);
        req.response().end(result);
    }

    private Map<String, RestFunction> funcMap = new HashMap<>();

    {
        funcMap.put("get-patient", this::getPatient);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        RestFunction f = funcMap.get(req.getParam("action"));
        if( f == null ){
            req.response().setStatusCode(404);
        } else {
            Connection conn = null;
            try {
                HttpServerResponse resp = req.response();
                resp.putHeader("content-type", "application/json; charset=UTF-8");
                conn = ds.getConnection();
                f.call(req, conn);
            } catch (Exception e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (Exception ex) {
                        logger.error("Rollback failed", ex);
                    }
                }
                throw new RuntimeException(e);
            }
        }
    }

}

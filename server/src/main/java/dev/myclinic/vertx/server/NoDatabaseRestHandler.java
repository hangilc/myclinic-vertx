package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.dto.*;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.vertx.core.Promise.promise;

class NoDatabaseRestHandler extends RestHandlerBase implements Handler<RoutingContext> {

    interface NoDatabaseRestFunction {
        void call(RoutingContext ctx, NoDatabaseImpl impl) throws Exception;
    }

    private final Map<String, NoDatabaseRestFunction> noDatabaseFuncMap = new HashMap<>();

    private final AppConfig appConfig;

    NoDatabaseRestHandler(AppConfig appConfig, ObjectMapper mapper){
        super(mapper);
        this.appConfig = appConfig;
    }

    private void listDiseaseExample(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        List<DiseaseExampleDTO> _value = impl.listDiseaseExample();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void listHokensho(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        List<String> _value = impl.listHokensho(patientId);
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private String cacheClinicInfo;

    private void getClinicInfo(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        if( cacheClinicInfo != null ){
            ctx.response().end(cacheClinicInfo);
        } else {
            HttpServerRequest req = ctx.request();
            appConfig.getClinicInfo()
                    .onComplete(ar -> {
                        if (ar.failed()) {
                            req.response().setStatusCode(500).end("Cannot get clinic info.");
                        } else {
                            cacheClinicInfo = jsonEncode(ar.result());
                            req.response().end(cacheClinicInfo);
                        }
                    });
        }
    }

    private void getMasterMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        StringResultDTO _value = impl.getMasterMapConfigFilePath();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getShinryouByoumeiMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        StringResultDTO _value = impl.getShinryouByoumeiMapConfigFilePath();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getHokensho(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        MultiMap params = req.params();
        int patientId = Integer.parseInt(params.get("patient-id"));
        String file = params.get("file");
        byte[] _value = impl.getHokensho(patientId, file);
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getReferList(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        List<ReferItemDTO> _value = impl.getReferList();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getNameMapConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        StringResultDTO _value = impl.getNameMapConfigFilePath();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getPowderDrugConfigFilePath(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        StringResultDTO _value = impl.getPowderDrugConfigFilePath();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }

    private void getPracticeConfig(RoutingContext ctx, NoDatabaseImpl impl) throws Exception {
        HttpServerRequest req = ctx.request();
        PracticeConfigDTO _value = impl.getPracticeConfig();
        String result = mapper.writeValueAsString(_value);
        req.response().end(result);
    }


    {
        noDatabaseFuncMap.put("list-disease-example", this::listDiseaseExample);
        noDatabaseFuncMap.put("list-hokensho", this::listHokensho);
        noDatabaseFuncMap.put("get-clinic-info", this::getClinicInfo);
        noDatabaseFuncMap.put("get-master-map-config-file-path", this::getMasterMapConfigFilePath);
        noDatabaseFuncMap.put("get-shinryou-byoumei-map-config-file-path", this::getShinryouByoumeiMapConfigFilePath);
        noDatabaseFuncMap.put("get-hokensho", this::getHokensho);
        noDatabaseFuncMap.put("get-refer-list", this::getReferList);
        noDatabaseFuncMap.put("get-name-map-config-file-path", this::getNameMapConfigFilePath);
        noDatabaseFuncMap.put("get-powder-drug-config-file-path", this::getPowderDrugConfigFilePath);
        noDatabaseFuncMap.put("get-practice-config", this::getPracticeConfig);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String action = routingContext.request().getParam("action");
        NoDatabaseRestFunction f = noDatabaseFuncMap.get(action);
        if( f == null ){
            routingContext.next();
        } else {
            try {
                routingContext.response().putHeader("content-type", "application/json; charset=UTF-8");
                NoDatabaseImpl impl = new NoDatabaseImpl(appConfig);
                f.call(routingContext,impl);
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}

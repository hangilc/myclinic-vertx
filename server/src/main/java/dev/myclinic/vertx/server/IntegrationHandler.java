package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.server.integration.FaxedShohousenHandler;
import dev.myclinic.vertx.server.integration.HoumonKangoHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

class IntegrationHandler {

    public static Router createRouter(Vertx vertx, ObjectMapper mapper) {
        Router router = Router.router(vertx);
        router.mountSubRouter("/faxed-shohousen-data", FaxedShohousenHandler.createRouter(vertx, mapper));
        router.mountSubRouter("/houmon-kango", HoumonKangoHandler.createRouter(vertx, mapper));
        return router;
    }

}

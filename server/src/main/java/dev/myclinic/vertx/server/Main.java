package dev.myclinic.vertx.server;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class Main {

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        Route restRoute = router.route("/json/:action");
        restRoute.blockingHandler(new RestHandler());
        server.requestHandler(router);
        server.webSocketHandler(ws -> {
            System.out.println("opened");
            ws.closeHandler(e -> System.out.println("closed"));
        });
        server.listen(28080);
    }

}

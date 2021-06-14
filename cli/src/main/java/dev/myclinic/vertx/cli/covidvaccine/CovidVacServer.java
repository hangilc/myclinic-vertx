package dev.myclinic.vertx.cli.covidvaccine;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;

public class CovidVacServer {

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        String certPath = System.getenv("MYCLINIC_SERVER_CERT");
        String privateKey = System.getenv("MYCLINIC_SERVER_PRIVATE_KEY");
        HttpServer server = vertx.createHttpServer(new HttpServerOptions()
                .setPemKeyCertOptions(new PemKeyCertOptions()
                        .setCertPath(certPath)
                        .setKeyPath(privateKey)
                )
                .setSsl(true)
        );
        server.requestHandler(router);
    }

}

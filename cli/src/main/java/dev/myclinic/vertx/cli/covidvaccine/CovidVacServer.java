package dev.myclinic.vertx.cli.covidvaccine;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class CovidVacServer {

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        HttpServer server = vertx.createHttpServer();
    }

}

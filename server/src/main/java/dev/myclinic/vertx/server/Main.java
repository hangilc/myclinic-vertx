package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.myclinic.vertx.houkatsukensa.HoukatsuKensa;
import dev.myclinic.vertx.mastermap.MasterMap;
import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.appconfig.FileBasedAppConfig;
import dev.myclinic.vertx.db.MysqlDataSourceConfig;
import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.db.TableSet;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import javax.sql.DataSource;

public class Main {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    private static final ObjectMapper yamlMapper;

    static {
        yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public static void main(String[] args) {
        MysqlDataSourceConfig mysqlConfig = new MysqlDataSourceConfig();
        DataSource ds = MysqlDataSourceFactory.create(mysqlConfig);
        TableSet ts = TableSet.create();
        Vertx vertx = Vertx.vertx();
        AppConfig config = createConfig(vertx);
        MasterMap masterMap = config.getMasterMap();
        HoukatsuKensa houkatsuKensa = config.getHoukatsuKensa();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        Route restRoute = router.route("/json/:action");
        restRoute.blockingHandler(new RestHandler(ds, ts, mapper, masterMap, houkatsuKensa));
        restRoute.handler(new NoDatabaseRestHandler(config, mapper, vertx, masterMap));
        restRoute.failureHandler(errorHandler);
        Router integrationRouter = IntegrationHandler.createRouter(vertx, mapper);
        router.mountSubRouter("/integration", integrationRouter);
        router.route("/*").failureHandler(errorHandler);
        Route portalRoute = router.route("/portal/*");
        boolean isDevMode = "dev".equals(System.getenv("VERTXWEB_ENVIRONMENT"));
        portalRoute.handler(StaticHandler.create(isDevMode ? "server/webroot/portal" : "webroot/portal")
                .setDefaultContentEncoding("UTF-8").setFilesReadOnly(!isDevMode)
                .setCachingEnabled(!isDevMode));
        router.route("/portal").handler(ctx -> ctx.response().setStatusCode(301)
                .putHeader("Location", "portal/index.html")
                .end());
        server.requestHandler(router);
        server.webSocketHandler(ws -> {
            System.out.println("opened");
            ws.closeHandler(e -> System.out.println("closed"));
        });
        int port = 28080;
        server.listen(port);
        System.out.println(String.format("server started at port %d", port));
    }

    private static AppConfig createConfig(Vertx vertx) {
        String configDir = System.getenv("MYCLINIC_CONFIG_DIR");
        if (configDir == null) {
            throw new RuntimeException("Cannot find env var: MYCLINIC_CONFIG_DIR");
        }
        return new FileBasedAppConfig(configDir, vertx);
    }

    private static Handler<RoutingContext> errorHandler = ctx -> {
        Throwable th = ctx.failure();
        th.printStackTrace();
        int statusCode = ctx.statusCode();
        if (statusCode < 0) {
            statusCode = 500;
        }
        ctx.response().setStatusCode(statusCode).end(th.getMessage());
    };

}

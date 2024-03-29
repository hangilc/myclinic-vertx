package dev.myclinic.vertx.server;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.appconfig.FileBasedAppConfig;
import dev.myclinic.vertx.db.MysqlDataSourceConfig;
import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.db.TableSet;
import dev.myclinic.vertx.drawer.JacksonOpDeserializer;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.houkatsukensa.HoukatsuKensa;
import dev.myclinic.vertx.mastermap.MasterMap;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class Main {
    // private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        module.addDeserializer(Op.class, new JacksonOpDeserializer());
        mapper.registerModule(module);
    }

    public static void main(String[] args) {
        CmdArgs cmdArgs = CmdArgs.parse(args);
        if( cmdArgs.simulateSlowDownload ){
            GlobalService.getInstance().setSimulateSlowDownload(true);
        }
        if( cmdArgs.simulateSlowUpload ){
            GlobalService.getInstance().setSimulateSlowUpload(true);
        }
        if( cmdArgs.simulateUploadFail ){
            GlobalService.getInstance().setSimulateUploadFail(true);
        }
        MysqlDataSourceConfig mysqlConfig = new MysqlDataSourceConfig();
        DataSource ds = MysqlDataSourceFactory.create(mysqlConfig);
        TableSet ts = TableSet.create();
        Vertx vertx = Vertx.vertx();
        AppConfig config = createConfig(vertx);
        MasterMap masterMap = config.getMasterMap();
        HoukatsuKensa houkatsuKensa = config.getHoukatsuKensa();
        HotlineStreamerVerticle hotlineVerticle = new HotlineStreamerVerticle();
        vertx.deployVerticle(hotlineVerticle);
        HotlineUpstreamVerticle hotlineUpstreamVerticle = new HotlineUpstreamVerticle();
        vertx.deployVerticle(hotlineUpstreamVerticle);
        Router router = Router.router(vertx);
        Route restRoute = router.route("/json/:action");
        restRoute.handler(BodyHandler.create());
        restRoute.blockingHandler(new RestHandler(ds, ts, mapper, masterMap, houkatsuKensa, vertx));
        restRoute.handler(new NoDatabaseRestHandler(config, mapper, vertx, masterMap));
        restRoute.failureHandler(errorHandler);
        Router integrationRouter = IntegrationHandler.createRouter(vertx, mapper);
        router.route("/integration/*").handler(BodyHandler.create());
        router.mountSubRouter("/integration", integrationRouter);
        router.route("/appoint/:action")
                .handler(BodyHandler.create())
                .blockingHandler(new AppointRestHandler());
        router.route("/*").failureHandler(errorHandler);
        Route portalRoute = router.route("/web/*");
        StaticHandler staticHandler;
        if (cmdArgs.isDev) {
            String webroot = CmdArgs.findWebroot();
            staticHandler = StaticHandler.create(webroot)
                    .setWebRoot(webroot)
                    .setDefaultContentEncoding("UTF-8")
                    .setFilesReadOnly(false)
                    .setCachingEnabled(false);
        } else {
            staticHandler = StaticHandler.create()
                    .setDefaultContentEncoding("UTF-8")
                    .setFilesReadOnly(false)
                    .setCachingEnabled(false);
        }
        portalRoute.handler(staticHandler);
        router.route("/practice").handler(ctx -> ctx.response().setStatusCode(301)
                .putHeader("Location", "/web/portal/index.html")
                .end());
        router.route("/reception").handler(ctx -> ctx.response().setStatusCode(301)
                .putHeader("Location", "/web/portal-reception/index.html")
                .end());
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router);
        server.webSocketHandler(ws -> {
            System.out.println("opened: " + ws.path());
            if( ws.path().equals("/hotline") ){
                hotlineVerticle.addClient(ws);
            } else {
                ws.reject();
            }
        });
        String bind = resolveBind(cmdArgs);
        int port = cmdArgs.port;
        server.listen(port, bind);
        System.out.printf("server listening to %s:%d\n", bind, port);

        String certPath = System.getenv("MYCLINIC_SERVER_CERT");
        String privateKey = System.getenv("MYCLINIC_SERVER_PRIVATE_KEY");
        if( certPath != null && privateKey != null ){
            HttpServer sslServer = vertx.createHttpServer(new HttpServerOptions()
                    .setPemKeyCertOptions(new PemKeyCertOptions()
                            .setCertPath(certPath)
                            .setKeyPath(privateKey)
                    )
                    .setSsl(true)
            );
            sslServer.requestHandler(router);
            sslServer.webSocketHandler(ws -> {
                System.out.println("opened: " + ws.path());
                if( ws.path().equals("/hotline") ){
                    hotlineVerticle.addClient(ws);
                } else {
                    ws.reject();
                }
                ws.closeHandler(e -> System.out.println("closed"));
            });
            String sslBind = resolveSslBind(cmdArgs);
            int sslPort = cmdArgs.sslPort;
            sslServer.listen(sslPort, sslBind);
            System.out.printf("server listening to %s:%d\n", sslBind, sslPort);
        }
    }

    private static AppConfig createConfig(Vertx vertx) {
        String configDir = System.getenv("MYCLINIC_CONFIG_DIR");
        if (configDir == null) {
            throw new RuntimeException("Cannot find env var: MYCLINIC_CONFIG_DIR");
        }
        return new FileBasedAppConfig(configDir, vertx);
    }

    private static final Handler<RoutingContext> errorHandler = ctx -> {
        Throwable th = ctx.failure();
        th.printStackTrace();
        int statusCode = ctx.statusCode();
        if (statusCode < 0) {
            statusCode = 500;
        }
        String msg = th.getMessage();
        ctx.response().setStatusCode(statusCode).end(msg);
    };

    private static String resolveBind(CmdArgs cmdArgs){
        String bind = cmdArgs.bind;
        if( bind == null ){
            bind = System.getenv("MYCLINIC_BIND");
        }
        if( bind == null ){
            bind = "0.0.0.0";
        }
        return bind;
    }

    private static String resolveSslBind(CmdArgs cmdArgs){
        String bind = cmdArgs.bind;
        if( bind == null ){
            bind = System.getenv("MYCLINIC_SSL_BIND");
        }
        if( bind == null ){
            bind = "0.0.0.0";
        }
        return bind;
    }

}

package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        module.addDeserializer(Op.class, new JacksonOpDeserializer());
        mapper.registerModule(module);
    }

    private static final ObjectMapper yamlMapper;

    static {
        yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    private static class CmdArgs {
        int port = 28080;
        String webroot = null;
        List<String> args = new ArrayList<>();

        public static CmdArgs parse(String[] args) {
            CmdArgs cmdArgs = new CmdArgs();
            int i = 0;
            first:
            for (i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "--port": {
                        cmdArgs.port = Integer.parseInt(args[++i]);
                        break;
                    }
                    case "--webroot": {
                        cmdArgs.webroot = args[++i];
                        break;
                    }
                    default: {
                        if (arg.startsWith("-")) {
                            System.err.println(String.format("Invalid option: %s", arg));
                            System.err.println();
                            usage();
                            System.exit(1);
                        } else {
                            break first;
                        }
                    }
                }
            }
            for (; i < args.length; i++) {
                cmdArgs.args.add(args[i]);
            }
            if( cmdArgs.webroot == null ){
                cmdArgs.webroot = findWebroot();
            }
            return cmdArgs;
        }

        public static String findWebroot(){
            List<String> lists = List.of("server/webroot", "webroot");
            for(String s: lists){
                if( Files.exists(Path.of(s)) ){
                    return s;
                }
            }
            System.err.println("Cannot find webroot.");
            System.exit(1);
            return null;
        }

        public static void usage() {
            System.err.println("Usage: server [options]");
            System.err.println("  options:");
            System.err.println("    --port PORT       server listening port");
        }
    }

    public static void main(String[] args) throws Exception {
        CmdArgs cmdArgs = CmdArgs.parse(args);
        String webroot = cmdArgs.webroot;
        MysqlDataSourceConfig mysqlConfig = new MysqlDataSourceConfig();
        DataSource ds = MysqlDataSourceFactory.create(mysqlConfig);
        TableSet ts = TableSet.create();
        Vertx vertx = Vertx.vertx();
        AppConfig config = createConfig(vertx);
        MasterMap masterMap = config.getMasterMap();
        HoukatsuKensa houkatsuKensa = config.getHoukatsuKensa();
        FaxStreamingVerticle faxStreamingVerticle = new FaxStreamingVerticle();
        vertx.deployVerticle(faxStreamingVerticle);
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        Route restRoute = router.route("/json/:action");
        restRoute.handler(BodyHandler.create());
        restRoute.blockingHandler(new RestHandler(ds, ts, mapper, masterMap, houkatsuKensa));
        restRoute.handler(new NoDatabaseRestHandler(config, mapper, vertx, masterMap));
        restRoute.failureHandler(errorHandler);
        Router integrationRouter = IntegrationHandler.createRouter(vertx, mapper);
        router.route("/integration/*").handler(BodyHandler.create());
        router.mountSubRouter("/integration", integrationRouter);
        router.route("/*").failureHandler(errorHandler);
        boolean isDevMode = "dev".equals(System.getenv("VERTXWEB_ENVIRONMENT"));

        Route portalRoute = router.route("/portal/*");
        portalRoute.handler(StaticHandler.create(webroot + "/portal")
                .setDefaultContentEncoding("UTF-8").setFilesReadOnly(!isDevMode)
                .setCachingEnabled(!isDevMode));
        router.route("/portal").handler(ctx -> ctx.response().setStatusCode(301)
                .putHeader("Location", "/portal/index.html")
                .end());

//        Route portal2Route = router.route("/portal2/*");
//        portal2Route.handler(StaticHandler.create("server/webroot/portal2")
//                .setDefaultContentEncoding("UTF-8").setFilesReadOnly(!isDevMode)
//                .setCachingEnabled(!isDevMode));
//        router.route("/portal2").handler(ctx -> ctx.response().setStatusCode(301)
//                .putHeader("Location", "/portal2/index.html")
//                .end());
        ensureAppDir(GlobalService.getInstance().portalTmpDirToken);
        ensureAppDir(GlobalService.getInstance().paperScanDirToken);
        server.requestHandler(router);
        server.webSocketHandler(ws -> {
            System.out.println("opened: " + ws.path());
            faxStreamingVerticle.addClient(ws);
            ws.closeHandler(e -> System.out.println("closed"));
        });
        int port = cmdArgs.port;
        server.listen(port);
        System.out.println(String.format("server started at port %d", port));
    }

    private static void ensureAppDir(String dirToken) throws IOException {
        Path path = GlobalService.getInstance().resolveAppPath(dirToken);
        if( !Files.exists(path) ){
            Files.createDirectories(path);
        }
    }

    private static void addStaticPath(Router router, String url, Path root){
        if( !url.endsWith("/") ){
            url += "/";
        }
        String urlPrefix = url;
        router.get(urlPrefix + "*").handler(ctx -> {
            try {
                String path = URLDecoder.decode(ctx.request().path(), StandardCharsets.UTF_8)
                        .substring(urlPrefix.length());
                ctx.response().sendFile(root.resolve(path).toString());
            } catch(Exception e){
                ctx.fail(e);
            }
        });
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

}

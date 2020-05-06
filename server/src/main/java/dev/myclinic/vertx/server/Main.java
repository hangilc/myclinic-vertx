package dev.myclinic.vertx.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.myclinic.vertx.appconfig.AppConfig;
import dev.myclinic.vertx.appconfig.FileBasedAppConfig;
import dev.myclinic.vertx.db.MysqlDataSourceConfig;
import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.db.TableSet;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

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

    public static void main(String[] args){
        MysqlDataSourceConfig mysqlConfig = new MysqlDataSourceConfig();
        DataSource ds = MysqlDataSourceFactory.create(mysqlConfig);
        TableSet ts = TableSet.create();
        Vertx vertx = Vertx.vertx();
        AppConfig config = createConfig(vertx);
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        Route restRoute = router.route("/json/:action");
        restRoute.blockingHandler(new RestHandler(ds, ts, mapper));
        restRoute.handler(new NoDatabaseRestHandler(config, mapper));
        server.requestHandler(router);
        server.webSocketHandler(ws -> {
            System.out.println("opened");
            ws.closeHandler(e -> System.out.println("closed"));
        });
        server.listen(28080);
    }

    private static AppConfig createConfig(Vertx vertx){
        String configDir = System.getenv("MYCLINIC_CONFIG_DIR");
        if( configDir == null ){
            throw new RuntimeException("Cannot find env var: MYCLINIC_CONFIG_DIR");
        }
        return new FileBasedAppConfig(configDir, vertx);
    }

}

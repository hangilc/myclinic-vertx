package dev.myclinic.vertx.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class MysqlDataSourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(dev.myclinic.vertx.db.MysqlDataSourceFactory.class);

    public static DataSource create(dev.myclinic.vertx.db.MysqlDataSourceConfig mysqlConfig){
        String url = String.format("jdbc:mysql://%s:%d/%s?zeroDateTimeBehavior=convertToNull" +
                        "&noDatetimeStringSync=true&useUnicode=true&characterEncoding=utf8" +
                        "&useSSL=%s&serverTimezone=JST",
                mysqlConfig.getHost(), mysqlConfig.getPort(), mysqlConfig.getDatabase(),
                mysqlConfig.getUseSsl() ? "true" : "false");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(mysqlConfig.getUser());
        config.setPassword(mysqlConfig.getPassword());
        config.setInitializationFailTimeout(-1);
        //config.setAutoCommit(false);
        HikariDataSource ds = new HikariDataSource(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("closing data souce");
            ds.close();
        }));
        return ds;
    }

}

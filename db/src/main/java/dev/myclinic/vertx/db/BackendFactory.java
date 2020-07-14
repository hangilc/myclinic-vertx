package dev.myclinic.vertx.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

public class BackendFactory {
    private static final Logger logger = LoggerFactory.getLogger(BackendFactory.class);

    public static BackendFactory create(){
        MysqlDataSourceConfig config = new MysqlDataSourceConfig();
        return create(config);
    }

    public static BackendFactory create(MysqlDataSourceConfig config){
        DataSource ds = MysqlDataSourceFactory.create(config);
        TableSet ts = TableSet.create();
        return new BackendFactory(ds, ts);
    }

    private final DataSource ds;
    private final TableSet ts;

    private BackendFactory(DataSource ds, TableSet ts) {
        this.ds = ds;
        this.ts = ts;
    }

    public <T> T withBackend(Function<Backend,T> f){
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            Query query = new Query(conn);
            Backend backend = new Backend(ts, query);
            T ret =  f.apply(backend);
            Connection c = conn;
            conn = null;
            c.commit();
            return ret;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    logger.error("Rollback failed", ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void withBckend(Consumer<Backend> f){
        Void c = withBackend(backend -> {
            f.accept(backend);
            return null;
        });
    }

}

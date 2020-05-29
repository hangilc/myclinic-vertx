package dev.myclinic.vertx.dbtest;

import dev.myclinic.vertx.db.*;
import dev.myclinic.vertx.dto.PatientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        MysqlDataSourceConfig config = new MysqlDataSourceConfig();
        DataSource ds = MysqlDataSourceFactory.create(config);
        Main main = new Main(ds);
        try {
            main.start();
            System.exit(0);
        } catch(Exception e){
            logger.error("Main start failed.", e);
            System.exit(1);
        }
    }

    private final DataSource ds;
    private final TableSet ts = TableSet.create();

    private Main(DataSource ds){
        this.ds = ds;
    }

    public interface Proc {
        void exec(Backend backend) throws Exception;
    }

    private void withBackend(Proc proc){
        Connection conn = null;
        try {
            conn = ds.getConnection();
            Query query = new Query(conn);
            Backend backend = new Backend(ts, query);
            proc.exec(backend);
        } catch(Exception e){
            throw new RuntimeException(e);
        } finally {
            if( conn != null ){
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    logger.error("Failed to close connection.", throwables);
                }
            }
        }
    }

    private void assertTestDatabase() {
        withBackend(backend -> {
            PatientDTO patient = backend.getPatient(1);
            if( patient == null ||
                    !("試験".equals(patient.lastName) && "データ".equals(patient.firstName)) ){
                throw new RuntimeException("Database is not for testing.");
            }
        });
    }

    private void start() throws Exception {
        assertTestDatabase();
        System.out.println("started");
    }

}

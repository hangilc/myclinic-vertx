package dev.myclinic.vertx.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.PatientDTO;

import javax.sql.DataSource;
import java.sql.Connection;

class Config {

    private static final Config instance = new Config();

    public static Config getInstance(){
        return instance;
    }

    private DataSource ds;
    private final TableSet ts = TableSet.create();
    private final ObjectMapper mapper = new ObjectMapper();

    private Config(){
        MysqlDataSourceConfig config = new MysqlDataSourceConfig();
        this.ds = MysqlDataSourceFactory.create(config);
        try (Connection conn = ds.getConnection()) {
            Query query = new Query(conn);
            Backend backend = new Backend(ts, query);
            PatientDTO patient = backend.getPatient(1);
            if( patient == null ||
                    !("試験".equals(patient.lastName) && "データ".equals(patient.firstName)) ){
                this.ds = null;
                System.err.println("******************************************");
                System.err.println("****** DATABASE IS NOT FOR TESTING! ******");
                System.err.println("******************************************");
                System.exit(1);
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Connection getConnection(){
        try {
            return ds.getConnection();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public TableSet getTableSet(){
        return ts;
    }

    public ObjectMapper getMapper(){
        return mapper;
    }

}

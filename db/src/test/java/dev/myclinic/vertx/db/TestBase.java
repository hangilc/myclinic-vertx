package dev.myclinic.vertx.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;

class TestBase {

    protected Connection conn;
    protected Backend backend;
    protected ObjectMapper mapper;

    @BeforeEach
    public void prepare() throws Exception {
        this.conn = Config.getInstance().getConnection();
        conn.setAutoCommit(false);
        Query query = new Query(conn);
        TableSet ts = Config.getInstance().getTableSet();
        this.backend = new Backend(ts, query);
        this.mapper = Config.getInstance().getMapper();
    }

    @AfterEach
    public void cleanup() throws Exception {
        if( this.conn != null ) {
            this.conn.commit();
            this.conn.close();
        }
    }

}

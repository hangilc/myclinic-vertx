package dev.myclinic.vertx.camelcomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.jdbc.MysqlDataSource;
import dev.myclinic.vertx.db.MysqlDataSourceConfig;
import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;

import javax.sql.DataSource;

public class Setupper {

    private final SimpleRegistry registry = new SimpleRegistry();

    private Setupper(){

    }

    public static Setupper create(){
        return new Setupper();
    }

    public Setupper addToRegistry(String name, Class<?> cls, Object value){
        registry.bind(name, cls, value);
        return this;
    }

    public Setupper addDefaultDataSource(){
        DataSource dataSource = MysqlDataSourceFactory.create(new MysqlDataSourceConfig());
        return addToRegistry("dataSource", DataSource.class, dataSource);
    }

    public Setupper addDefaultObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        return addToRegistry("objectMapper", ObjectMapper.class, mapper);
    }

    public CamelContext setup(){
        CamelContext context = new DefaultCamelContext(registry);
        return context;
    }

}

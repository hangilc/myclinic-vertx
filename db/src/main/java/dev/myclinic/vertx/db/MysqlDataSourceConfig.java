package dev.myclinic.vertx.db;

public class MysqlDataSourceConfig {

    private String host;
    private int port = 3306;
    private String database = "myclinic";
    private boolean useSsl = false;
    private String user = System.getenv("MYCLINIC_DB_USER");
    private String password = System.getenv("MYCLINIC_DB_PASS");

    public MysqlDataSourceConfig(){
        this.host = System.getenv("MYCLINIC_DB_HOST");
        if( this.host == null ){
            this.host = "127.0.0.1";
        }
        String portEnv = System.getenv("MYCLINIC_DB_PORT");
        if( portEnv != null ){
            this.port = Integer.parseInt(portEnv);
        }
    }

    public dev.myclinic.vertx.db.MysqlDataSourceConfig port(int port){
        this.port = port;
        return this;
    }

    public dev.myclinic.vertx.db.MysqlDataSourceConfig database(String database){
        this.database = database;
        return this;
    }

    public dev.myclinic.vertx.db.MysqlDataSourceConfig user(String user){
        this.user = user;
        return this;
    }

    public dev.myclinic.vertx.db.MysqlDataSourceConfig password(String password){
        this.password = password;
        return this;
    }

    public dev.myclinic.vertx.db.MysqlDataSourceConfig useSsl(){
        this.useSsl = true;
        return this;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public boolean getUseSsl() {
        return useSsl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "MysqlDataSourceConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                ", useSsl=" + useSsl +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}

package dev.myclinic.vertx.db;

public class MysqlDialect {

    public String isValidAt(String validFrom, String validUpto, String at) {
        return String.format("(%s <= date(%s) and (%s = '0000-00-00' or %s >= date(%s)))",
                validFrom, at, validUpto, validUpto, at);
    }

    public String isValidUptoUnbound(String validUpto) {
        return String.format("(%s = '0000-00-00')", validUpto);
    }
}

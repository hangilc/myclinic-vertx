package dev.myclinic.vertx.master.csv;

import java.sql.SQLException;
import java.sql.Statement;

class MasterHandler {

    private Statement stmt;

    MasterHandler(Statement stmt) {
        this.stmt = stmt;
    }

    boolean enterShinryouMaster(ShinryouMasterCSV shinryouCSV, String validFrom) throws SQLException {
        int kubun = shinryouCSV.kubun;
        if (kubun == 0 || kubun == 3 || kubun == 5) {
            String sql = shinryouSql(shinryouCSV, validFrom);
            stmt.executeUpdate(sql);
            return true;
        } else {
            return false;
        }

    }

    boolean enterIyakuhinMaster(IyakuhinMasterCSV iyakuhinCSV, String validFrom) throws SQLException {
        int kubun = iyakuhinCSV.kubun;
        if (kubun == 0 || kubun == 3 || kubun == 5) {
            String sql = iyakuhinSql(iyakuhinCSV, validFrom);
            stmt.executeUpdate(sql);
            return true;
        } else {
            return false;
        }

    }

    boolean enterKizaiMaster(KizaiMasterCSV kizaiCSV, String validFrom) throws SQLException {
        int kubun = kizaiCSV.kubun;
        if (kubun == 0 || kubun == 3 || kubun == 5) {
            String sql = kizaiSql(kizaiCSV, validFrom);
            stmt.executeUpdate(sql);
            return true;
        } else {
            return false;
        }

    }

    boolean enterShoubyoumeiMaster(ShoubyoumeiMasterCSV shoubyoumeiCSV, String validFrom) throws SQLException {
        int kubun = shoubyoumeiCSV.kubun;
        if (kubun == 0 || kubun == 3 || kubun == 5) {
            String sql = shoubyoumeiSql(shoubyoumeiCSV, validFrom);
            stmt.executeUpdate(sql);
            return true;
        } else {
            return false;
        }

    }

    boolean enterShuushokugoMaster(ShuushokugoMasterCSV shuushokugoCSV, String validFrom) throws SQLException {
        int kubun = shuushokugoCSV.kubun;
        if (kubun == 0 || kubun == 3 || kubun == 5) {
            String sql = shuushokugoSql(shuushokugoCSV, validFrom);
            stmt.executeUpdate(sql);
            return true;
        } else {
            return false;
        }

    }

    private static String shinryouTemplate;

    static {
        shinryouTemplate = String.join(" ",
                "insert into shinryoukoui_master_arch set",
                "shinryoucode=%s,",
                "name='%s',",
                "tensuu='%s',",
                "tensuu_shikibetsu='%s',",
                "shuukeisaki='%s',",
                "houkatsukensa='%s',",
                "oushinkubun='%s',",
                "kensagroup='%s',",
                "valid_from='%s',",
                "valid_upto='%s';"
        );
    }

    private static String shinryouSql(ShinryouMasterCSV master, String validFrom) {
        return String.format(shinryouTemplate,
                master.shinryoucode,
                master.name,
                master.tensuu,
                master.tensuuShikibetsu,
                master.shuukeisaki,
                master.houkatsukensa,
                master.oushinKubun,
                master.kensaGroup,
                validFrom,
                "0000-00-00"
        );
    }

    private static String iyakuhinTemplate;

    static {
        iyakuhinTemplate = String.join(" ",
                "insert into iyakuhin_master_arch set ",
                "iyakuhincode=%d,",
                "yakkacode='%s',",
                "name='%s',",
                "yomi='%s',",
                "unit='%s',",
                "yakka='%s',",
                "madoku='%s',",
                "kouhatsu='%s',",
                "zaikei='%s',",
                "valid_from='%s',",
                "valid_upto='%s';");
    }

    private static String iyakuhinSql(IyakuhinMasterCSV master, String validFrom) {
        return String.format(iyakuhinTemplate,
                master.iyakuhincode,
                master.yakkacode,
                master.name,
                master.yomi,
                master.unit,
                master.yakka,
                master.madoku,
                master.kouhatsu,
                master.zaikei,
                validFrom,
                "0000-00-00");
    }

    private static String kizaiTemplate;

    static {
        kizaiTemplate = String.join(" ",
                "insert into tokuteikizai_master_arch set ",
                "kizaicode=%d,",
                "name='%s',",
                "yomi='%s',",
                "unit='%s',",
                "kingaku='%s',",
                "valid_from='%s',",
                "valid_upto='%s';"
        );
    }

    private static String kizaiSql(KizaiMasterCSV master, String validFrom) {
        return String.format(kizaiTemplate,
                master.kizaicode,
                master.name,
                master.yomi,
                master.unit,
                master.kingaku,
                validFrom,
                "0000-00-00"
        );
    }

    private static String shoubyoumeiTemplate;

    static {
        shoubyoumeiTemplate = String.join(" ",
                "insert into shoubyoumei_master_arch set ",
                "shoubyoumeicode=%d,",
                "name='%s',",
                "valid_from='%s',",
                "valid_upto='%s';"
        );
    }

    private static String shoubyoumeiSql(ShoubyoumeiMasterCSV master, String validFrom) {
        return String.format(shoubyoumeiTemplate,
                master.shoubyoumeicode,
                master.name,
                validFrom,
                "0000-00-00"
        );
    }

    private static String shuushokugoTemplate;

    static {
        shuushokugoTemplate = String.join(" ",
                "insert into shuushokugo_master set ",
                "shuushokugocode=%d,",
                "name='%s'"
        );
    }

    private static String shuushokugoSql(ShuushokugoMasterCSV master, String validFrom){
        return String.format(shuushokugoTemplate,
                master.shuushokugocode,
                master.name
        );
    }


}

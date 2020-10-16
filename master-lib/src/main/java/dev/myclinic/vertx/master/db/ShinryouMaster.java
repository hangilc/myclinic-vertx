package dev.myclinic.vertx.master.db;

import dev.myclinic.vertx.master.csv.ShinryouMasterCSV;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShinryouMaster {

    public int shinryoucode;
    public String name;
    public String tensuu;
    public String tensuuShikibetsu;
    public String shuukeisaki;
    public String houkatsukensa;
    public String oushinkubun;
    public String kensagroup;
    public String validFrom;
    public String validUpto;

    public static ShinryouMaster fromResultSet(ResultSet rs) throws SQLException {
        ShinryouMaster m = new ShinryouMaster();
        m.shinryoucode = rs.getInt("shinryoucode");
        m.name = rs.getString("name");
        m.tensuu = rs.getString("tensuu");
        m.tensuuShikibetsu = rs.getString("tensuu_shikibetsu");
        m.shuukeisaki = rs.getString("shuukeisaki");
        m.houkatsukensa = rs.getString("houkatsukensa");
        m.oushinkubun = rs.getString("oushinkubun");
        m.kensagroup = rs.getString("kensagroup");
        m.validFrom = rs.getString("valid_from");
        m.validUpto = rs.getString("valid_upto");
        return m;
    }

    public static ShinryouMaster fromCSV(ShinryouMasterCSV csv){
        ShinryouMaster m = new ShinryouMaster();
        m.shinryoucode = csv.shinryoucode;
        m.name = csv.name;
        m.tensuu = csv.tensuu;
        m.tensuuShikibetsu = csv.tensuuShikibetsu;
        m.shuukeisaki = csv.shuukeisaki;
        m.houkatsukensa = csv.houkatsukensa;
        m.oushinkubun = csv.oushinKubun;
        m.kensagroup = csv.kensaGroup;
        return m;
    }

    public boolean equalsWithCSV(ShinryouMasterCSV csv){
        return this.shinryoucode == csv.shinryoucode &&
        this.name.equals(csv.name) &&
        this.tensuu.equals(csv.tensuu) &&
        this.tensuuShikibetsu.equals(csv.tensuuShikibetsu) &&
        this.shuukeisaki.equals(csv.shuukeisaki) &&
        this.houkatsukensa.equals(csv.houkatsukensa) &&
        this.oushinkubun.equals(csv.oushinKubun) &&
        this.kensagroup.equals(csv.kensaGroup);
    }

    @Override
    public String toString() {
        return "ShinryouMaster{" +
                "shinryoucode=" + shinryoucode +
                ", name='" + name + '\'' +
                ", tensuu='" + tensuu + '\'' +
                ", tensuuShikibetsu='" + tensuuShikibetsu + '\'' +
                ", shuukeisaki='" + shuukeisaki + '\'' +
                ", houkatsukensa='" + houkatsukensa + '\'' +
                ", oushinkubun='" + oushinkubun + '\'' +
                ", kensagroup='" + kensagroup + '\'' +
                ", validFrom='" + validFrom + '\'' +
                ", validUpto='" + validUpto + '\'' +
                '}';
    }
}

package dev.myclinic.vertx.master.csv;

public class ShinryouMasterCSV {
    public int kubun;
    public String masterShubetsu;
    public String nyuuGaiTekiyou;
    public String byouShinKubun;
    public String tensuuShikibetsu;
    public int shinryoucode;
    public String name;
    public String tensuu;
    public String shuukeisaki;
    public String houkatsukensa;
    public String oushinKubun;
    public String kensaGroup;

    public ShinryouMasterCSV(){

    }

    public ShinryouMasterCSV(CSVRow row) {
        kubun = row.getInt(1);
        masterShubetsu = row.getString(2);
        nyuuGaiTekiyou = row.getString(13);
        byouShinKubun = row.getString(19);
        tensuuShikibetsu = row.getString(11);
        shinryoucode = row.getInt(3);
        name = row.getString(5);
        tensuu = row.getString(12);
        shuukeisaki = row.getString(15);
        houkatsukensa = twoChars(row.getString(16));
        oushinKubun = row.getString(17);
        kensaGroup = twoChars(row.getString(51));
    }

    private String twoChars(String s){
        if( s.length() == 1 ){
            return "0" + s;
        } else {
            return s;
        }
    }

    @Override
    public String toString() {
        return "ShinryouMasterCSV{" +
                "kubun=" + kubun +
                ", masterShubetsu='" + masterShubetsu + '\'' +
                ", nyuuGaiTekiyou='" + nyuuGaiTekiyou + '\'' +
                ", byouShinKubun='" + byouShinKubun + '\'' +
                ", tensuuShikibetsu='" + tensuuShikibetsu + '\'' +
                ", shinryoucode=" + shinryoucode +
                ", name='" + name + '\'' +
                ", tensuu='" + tensuu + '\'' +
                ", shuukeisaki='" + shuukeisaki + '\'' +
                ", houkatsukensa='" + houkatsukensa + '\'' +
                ", oushinKubun='" + oushinKubun + '\'' +
                ", kensaGroup='" + kensaGroup + '\'' +
                '}';
    }
}
package dev.myclinic.vertx.master.csv;

public class IyakuhinMasterCSV {
    public int kubun;
    public String masterShubetsu;
    public int iyakuhincode;
    public String name;
    public String yomi;
    public String unit;
    public int kingakuShubetsu;
    public String yakka;
    public int madoku;
    public int kouhatsu;
    public int zaikei;
    public String yakkacode;

    public IyakuhinMasterCSV(CSVRow row){
        kubun = row.getInt(1);
        masterShubetsu = row.getString(2);
        iyakuhincode = row.getInt(3);
        name = row.getString(5);
        yomi = row.getString(7);
        unit = row.getString(10);
        kingakuShubetsu = row.getInt(11);
        yakka = row.getString(12);
        madoku = row.getInt(14);
        kouhatsu = row.getInt(17);
        zaikei = row.getInt(28);
        yakkacode = row.getString(32);
    }

}


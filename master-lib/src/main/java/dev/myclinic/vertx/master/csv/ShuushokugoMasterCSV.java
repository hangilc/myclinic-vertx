package dev.myclinic.vertx.master.csv;

public class ShuushokugoMasterCSV {

    public int kubun;
    public int shuushokugocode;
    public String name;

    public ShuushokugoMasterCSV(CSVRow row) {
        kubun = row.getInt(1);
        shuushokugocode = row.getInt(3);
        name = row.getString(7);
    }

}

package dev.myclinic.vertx.master.csv;

public class ShoubyoumeiMasterCSV {

    public int kubun;
    public int shoubyoumeicode;
    public String name;

    public ShoubyoumeiMasterCSV(CSVRow row) {
        kubun = row.getInt(1);
        shoubyoumeicode = row.getInt(3);
        name = row.getString(6);
    }

}

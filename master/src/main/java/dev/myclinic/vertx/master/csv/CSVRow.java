package dev.myclinic.vertx.master.csv;

public interface CSVRow {
	String getString(int index);
	default int getInt(int index) throws NumberFormatException {
		return Integer.parseInt(getString(index));
	}
}
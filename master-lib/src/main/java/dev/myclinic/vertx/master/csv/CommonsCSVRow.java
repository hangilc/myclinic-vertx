package dev.myclinic.vertx.master.csv;

import org.apache.commons.csv.CSVRecord;

public class CommonsCSVRow implements dev.myclinic.vertx.master.csv.CSVRow {
	private CSVRecord record;

	public CommonsCSVRow(CSVRecord record){
		this.record = record;
	}

	public String getString(int index){
		return this.record.get(index-1);
	}

}
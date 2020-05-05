package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.Primary;

public class IyakuhinMasterDTO {
	@Primary
	public int iyakuhincode;
	@Primary
	public String validFrom;
	public String name;
	public String yomi;
	public String unit;
	public double yakka;
	public char madoku;
	public char kouhatsu;
	public char zaikei;
	public String validUpto;

	@Override
	public String toString(){
		return "IyakuhinMasterDTO[" + 
			"iyakuhincode=" + iyakuhincode + "," +
			"validFrom=" + validFrom + "," +
			"name=" + name + "," +
			"yomi=" + yomi + "," +
			"unit=" + unit + "," +
			"yakka=" + yakka + "," +
			"madoku=" + madoku + "," +
			"kouhatsu=" + kouhatsu + "," +
			"zaikei=" + zaikei + "," +
			"validUpto=" + validUpto +
		"]";
	}
}
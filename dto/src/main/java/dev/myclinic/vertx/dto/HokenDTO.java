package dev.myclinic.vertx.dto;

public class HokenDTO {
	public dev.myclinic.vertx.dto.ShahokokuhoDTO shahokokuho;
	public dev.myclinic.vertx.dto.KoukikoureiDTO koukikourei;
	public dev.myclinic.vertx.dto.RoujinDTO roujin;
	public KouhiDTO kouhi1;
	public KouhiDTO kouhi2;
	public KouhiDTO kouhi3;

	public static dev.myclinic.vertx.dto.HokenDTO copy(dev.myclinic.vertx.dto.HokenDTO src){
		dev.myclinic.vertx.dto.HokenDTO dst = new dev.myclinic.vertx.dto.HokenDTO();
		dst.shahokokuho = src.shahokokuho;
		dst.koukikourei = src.koukikourei;
		dst.roujin = src.roujin;
		dst.kouhi1 = src.kouhi1;
		dst.kouhi2 = src.kouhi2;
		dst.kouhi3 = src.kouhi3;
		return dst;
	}

	@Override
	public String toString() {
		return "HokenDTO{" +
				"shahokokuho=" + shahokokuho +
				", koukikourei=" + koukikourei +
				", roujin=" + roujin +
				", kouhi1=" + kouhi1 +
				", kouhi2=" + kouhi2 +
				", kouhi3=" + kouhi3 +
				'}';
	}
}
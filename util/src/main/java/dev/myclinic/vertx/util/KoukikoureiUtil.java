package dev.myclinic.vertx.util;

import dev.myclinic.vertx.dto.KoukikoureiDTO;

public class KoukikoureiUtil {

	public static String rep(KoukikoureiDTO koukikoureiDTO){
		return rep(koukikoureiDTO.futanWari);
	}

	public static String rep(int futanWari){
		return "後期高齢" + futanWari + "割";
	}

	public static String hokenshaBangouString(int hokenshaBangou){
		return String.format("%08d", hokenshaBangou);
	}

	public static String hihokenshaBangouString(int hihokenshaBangou){
		return String.format("%08d", hihokenshaBangou);
	}
}

package dev.myclinic.vertx.util;

import dev.myclinic.vertx.dto.KoukikoureiDTO;

public class KoukikoureiUtil {

	public static String rep(KoukikoureiDTO koukikoureiDTO){
		return rep(koukikoureiDTO.futanWari);
	}

	public static String rep(int futanWari){
		return "後期高齢" + futanWari + "割";
	}

}

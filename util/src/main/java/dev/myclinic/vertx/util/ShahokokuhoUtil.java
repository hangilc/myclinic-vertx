package dev.myclinic.vertx.util;

import dev.myclinic.vertx.dto.ShahokokuhoDTO;

public class ShahokokuhoUtil {

	public static String rep(ShahokokuhoDTO shahokokuhoDTO){
		return rep(shahokokuhoDTO.hokenshaBangou, shahokokuhoDTO.kourei);
	}

	public static String rep(int hokenshaBangou, int koureiFutan){
		String name = name(hokenshaBangou);
		if( koureiFutan > 0 ){
			return name + "・高齢" + koureiFutan + "割";
		} else {
			return name;
		}
	}

	public static String name(int hokenshaBangou){
		if( hokenshaBangou <= 9999 )
			return "政管健保";
		if( hokenshaBangou <= 999999 )
			return "国保";
		switch(hokenshaBangou/1000000){
			case 1: return "協会けんぽ";
			case 2: return "船員";
			case 3: return "日雇一般";
			case 4: return "日雇特別";
			case 6: return "組合健保";
			case 7: return "自衛官";
			case 31: return "国家公務員共済";
			case 32: return "地方公務員共済";
			case 33: return "警察公務員共済";
			case 34: return "学校共済";
			case 63: return "特定健保退職";
			case 67: return "国保退職";
			case 72: return "国家公務員共済退職";
			case 73: return "地方公務員共済退職";
			case 74: return "警察公務員共済退職";
			case 75: return "学校共済退職";
			default: return "不明";
		}
	}

	public static String hokenshaBangouRep(int hokenshaBangou){
		if( hokenshaBangou <= 999999 ){
			return String.format("%06d", hokenshaBangou);
		} else {
			return String.format("%08d", hokenshaBangou);
		}
	}

	public static String hihokenshaRep(ShahokokuhoDTO shahokokuho){
		String kigou = shahokokuho.hihokenshaKigou;
		String bangou = shahokokuho.hihokenshaBangou;
		if( kigou == null || kigou.equals("") ){
			return bangou;
		}
		if( bangou == null || bangou.equals("") ){
			return kigou;
		}
		return kigou + " ・ " + bangou;
	}

}
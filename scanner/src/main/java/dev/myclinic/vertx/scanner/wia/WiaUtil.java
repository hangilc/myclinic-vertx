package dev.myclinic.vertx.scanner.wia;

class WiaUtil {

	public static int HIWORD(int dWord){
		return dWord >>> 16;
	}

	public static int LOWORD(int dWord){
		return dWord & 0xffff;
	}

	public static int GET_STIDEVICE_TYPE(int dwDevType){
		return HIWORD(dwDevType);
	}

	public static int GET_STIDEVICE_SUBTYPE(int dwDevType){
		return LOWORD(dwDevType);
	}

}
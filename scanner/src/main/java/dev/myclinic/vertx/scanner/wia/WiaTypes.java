package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.WinDef.ULONG;

public interface WiaTypes {

	public static class PROPID extends ULONG {

		public PROPID(){

		}

		public PROPID(long value){
			super(value);
		}

	}

}
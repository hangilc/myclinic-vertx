package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public class WiaDataTransfer extends Unknown implements IWiaDataTransfer {

	public static class ByReference extends WiaDataTransfer implements Structure.ByReference {}

	public WiaDataTransfer(){}

	public WiaDataTransfer(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT idtGetData(STGMEDIUM medium, WiaDataCallback callback){
		return (HRESULT)_invokeNativeObject(3, new Object[]{
			this.getPointer(), medium, callback
		}, HRESULT.class);
	}

}

package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class WiaDataCallback extends Unknown implements IWiaDataCallback {

	public static class ByReference extends WiaDataCallback implements Structure.ByReference {}

	public WiaDataCallback(){}

	public WiaDataCallback(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT BandedDataCallback(LONG lMessage, LONG lStatus, LONG lPercentComplete,
		LONG lOffset, LONG lLength, LONG lReserved, LONG lResLength, PointerByReference pbBuffer){
		return (HRESULT)_invokeNativeObject(3, new Object[]{
			this.getPointer(), lMessage, lStatus, lPercentComplete, lOffset, lLength,
			lReserved, lResLength, pbBuffer
		}, HRESULT.class);
	}

}


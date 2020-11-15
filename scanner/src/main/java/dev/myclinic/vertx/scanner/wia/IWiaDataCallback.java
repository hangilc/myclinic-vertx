package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public interface IWiaDataCallback extends IUnknown {

	public static final IID IID_IWiaDataCallback = new IID("a558a866-a5b0-11d2-a08f-00c04f72dc3c");

	HRESULT BandedDataCallback(LONG lMessage, LONG lStatus, LONG lPercentComplete,
		LONG lOffset, LONG lLength, LONG lReserved, LONG lResLength, PointerByReference pbBuffer);
}
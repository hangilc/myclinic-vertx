package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public interface IWiaDataTransfer extends IUnknown {

	static final IID IID_IWiaDataTransfer = 
		new IID("a6cef998-a5b0-11d2-a08f-00c04f72dc3c");

	HRESULT idtGetData(STGMEDIUM medium, WiaDataCallback callback);

}
package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;

public interface IEnumWIA_DEV_INFO extends IUnknown {

	public static final IID IID_IEnumWIA_DEV_INFO = new IID("5e38b83c-8cf1-11d1-bf92-0060081ed811");

	HRESULT Next(ULONG n, WiaPropertyStorage.ByReference[] storage, IntByReference nFetched);
	HRESULT GetCount(IntByReference pp);
}
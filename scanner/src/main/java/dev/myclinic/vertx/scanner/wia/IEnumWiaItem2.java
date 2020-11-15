package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;

public interface IEnumWiaItem2 extends IUnknown {

	HRESULT Next(ULONG n, WiaItem2.ByReference[] items, IntByReference nFetched);
}
package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public interface IEnumSTATPROPSTG extends IUnknown {

	HRESULT Next(ULONG celt, STATPROPSTG[] rgelt, ULONGByReference pceltFetched);
}
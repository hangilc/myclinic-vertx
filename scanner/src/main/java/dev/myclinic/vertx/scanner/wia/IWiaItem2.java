package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;

public interface IWiaItem2 extends IUnknown {

	int GetItemType();
	EnumWiaItem2 EnumChildItems(GUID category);
	HRESULT DeviceDlg(HWND parent, LONG flags, LONG intent, 
		IntByReference itemCount, WiaItem2.ByReference[] items);
}
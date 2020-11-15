package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IWiaItem extends IUnknown {

	HRESULT GetItemType(LONGByReference pItemType);
	HRESULT EnumChildItems(PointerByReference ppIEnumWiaItem);
	HRESULT DeviceDlg(HWND parent, LONG flags, LONG intent, 
		IntByReference itemCount, PointerByReference items);
}
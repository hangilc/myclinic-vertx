package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public interface IWiaPropertyStorage extends IUnknown, WiaTypes {

	public static final IID IID_IWiaPropertyStorage = new IID("98B5E8A0-29CC-491a-AAC0-E6DB4FDCCEB6");

	HRESULT GetCount(ULONGByReference pulNumProps);
	HRESULT Enum(PointerByReference ppenum);
	HRESULT ReadMultiple(int n, PROPSPEC[] propspecs, PROPVARIANT[] propvars);
	HRESULT WriteMultiple(ULONG n, PROPSPEC[] specs, PROPVARIANT[] values, PROPID propidNameFirst);
}
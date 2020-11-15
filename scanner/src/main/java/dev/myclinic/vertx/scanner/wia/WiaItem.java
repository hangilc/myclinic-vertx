package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class WiaItem extends Unknown implements IWiaItem {

	public static class ByReference extends WiaItem implements Structure.ByReference {}

	public WiaItem(){}

	public WiaItem(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT GetItemType(LONGByReference pItemType){
		return (HRESULT)_invokeNativeObject(3, new Object[]{
			this.getPointer(), pItemType
		}, HRESULT.class);
	}

	@Override
	public HRESULT EnumChildItems(PointerByReference ppIEnumWiaItem){
		return (HRESULT)_invokeNativeObject(5, new Object[]{
			this.getPointer(), ppIEnumWiaItem
		}, HRESULT.class);
	}

	@Override
	public HRESULT DeviceDlg(HWND parent, LONG flags, LONG intent, 
		IntByReference itemCount, PointerByReference items){
		return (HRESULT)_invokeNativeObject(10, new Object[]{
			this.getPointer(), parent, flags, intent, itemCount, items
		}, HRESULT.class);
	}


}

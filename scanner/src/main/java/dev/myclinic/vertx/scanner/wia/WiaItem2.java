package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class WiaItem2 extends Unknown implements IWiaItem2 {

	public static class ByReference extends WiaItem2 implements Structure.ByReference {}

	public WiaItem2(){}

	public WiaItem2(Pointer pointer){
		super(pointer);
	}

	@Override
	public int GetItemType(){
		IntByReference intPtr = new IntByReference();
		HRESULT hr = (HRESULT)_invokeNativeObject(8, new Object[]{
			this.getPointer(), intPtr
		}, HRESULT.class);
		COMUtils.checkRC(hr);
		return intPtr.getValue();
	}

	@Override
	public EnumWiaItem2 EnumChildItems(GUID category){
		PointerByReference ptr = new PointerByReference();
		HRESULT hr = (HRESULT)_invokeNativeObject(5, new Object[]{
			this.getPointer(), category, ptr
		}, HRESULT.class);
		COMUtils.checkRC(hr);
		return new EnumWiaItem2(ptr.getValue());
	}

	@Override
	public HRESULT DeviceDlg(HWND parent, LONG flags, LONG intent, 
		IntByReference itemCount, WiaItem2.ByReference[] items){
		return (HRESULT)_invokeNativeObject(9, new Object[]{
			this.getPointer(), parent, flags, intent, itemCount, items
		}, HRESULT.class);
	}


}

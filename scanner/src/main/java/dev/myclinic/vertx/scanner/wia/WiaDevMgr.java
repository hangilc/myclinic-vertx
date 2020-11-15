package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class WiaDevMgr extends Unknown implements IWiaDevMgr {

	public WiaDevMgr(){

	}

	public WiaDevMgr(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT EnumDeviceInfo(long flag, PointerByReference pp){
		return (HRESULT) this._invokeNativeObject(3,
			new Object[]{ this.getPointer(), new NativeLong(flag), pp }, HRESULT.class);
	}

	@Override
	public HRESULT CreateDevice(BSTR deviceID, PointerByReference pp){
		return (HRESULT) this._invokeNativeObject(4,
			new Object[]{ this.getPointer(), deviceID, pp }, HRESULT.class);
	}

	@Override
	public HRESULT SelectDeviceDlgID(HWND hwndParent, LONG lDeviceType, LONG lFlags, PointerByReference pbstrDeviceID){
		return (HRESULT) this._invokeNativeObject(6,
			new Object[]{ this.getPointer(), hwndParent, lDeviceType, lFlags, pbstrDeviceID }, HRESULT.class);
	}

	@Override
	public HRESULT GetImageDlg(HWND hwndParent, LONG lDeviceType, LONG lFlags, LONG lIntent,
                               IWiaItem pItemRoot, BSTR bstrFileName, GUID pguidFormat){
		return (HRESULT) this._invokeNativeObject(7,
			new Object[]{ this.getPointer(), hwndParent, lDeviceType, lFlags,
				lIntent, pItemRoot, bstrFileName, pguidFormat }, HRESULT.class);
	}

}
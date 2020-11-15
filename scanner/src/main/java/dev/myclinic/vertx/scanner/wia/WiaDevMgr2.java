package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class WiaDevMgr2 extends Unknown implements IWiaDevMgr2 {

	public WiaDevMgr2(){

	}

	public WiaDevMgr2(Pointer pointer){
		super(pointer);
	}

	@Override
	public EnumWIA_DEV_INFO EnumDeviceInfo(int flag){
		PointerByReference pp = new PointerByReference();
		HRESULT hr = (HRESULT) this._invokeNativeObject(3,
			new Object[]{ this.getPointer(), new LONG(flag), pp }, HRESULT.class);
		COMUtils.checkRC(hr);
		return new EnumWIA_DEV_INFO(pp.getValue());
	}

	@Override
	public WiaItem2 CreateDevice(BSTR deviceID){
		PointerByReference pp = new PointerByReference();
		HRESULT hr = (HRESULT) this._invokeNativeObject(4,
			new Object[]{ this.getPointer(), new LONG(0), deviceID, pp }, HRESULT.class);
		if( hr.intValue() != 0 ){
			System.err.printf("HRESULT: %x\n", hr.intValue());
		}
		COMUtils.checkRC(hr);
		return new WiaItem2(pp.getValue());
	}
}
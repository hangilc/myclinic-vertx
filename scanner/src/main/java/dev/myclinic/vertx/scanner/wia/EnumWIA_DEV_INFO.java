package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;

public class EnumWIA_DEV_INFO extends Unknown implements IEnumWIA_DEV_INFO {

	public EnumWIA_DEV_INFO(){

	}

	public EnumWIA_DEV_INFO(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT Next(ULONG n, WiaPropertyStorage.ByReference[] storage, IntByReference nFetched){
		return (HRESULT)_invokeNativeObject(3, new Object[]{
			this.getPointer(), n, storage, nFetched
		}, HRESULT.class);
	}

	@Override
	public HRESULT GetCount(IntByReference pp){
		return (HRESULT)_invokeNativeObject(7, new Object[]{
			this.getPointer(), pp
		}, HRESULT.class);
	}

}
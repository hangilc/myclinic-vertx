package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;

public class EnumWiaItem2 extends Unknown implements IEnumWiaItem2 {

	public EnumWiaItem2(){}

	public EnumWiaItem2(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT Next(ULONG n, WiaItem2.ByReference[] items, IntByReference nFetched){
		throw new RuntimeException("not implemented yet");
		// HRESULT hr = (HRESULT)_invokeNativeObject(3, new Object[]{
		// 	this.getPointer(), n, items, nFetched
		// }, HRESULT.class);
		// return hr;
	}

}

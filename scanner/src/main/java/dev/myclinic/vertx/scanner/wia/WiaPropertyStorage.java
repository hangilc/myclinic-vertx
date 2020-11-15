package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

public class WiaPropertyStorage extends Unknown implements IWiaPropertyStorage {

	public static class ByReference extends WiaPropertyStorage implements Structure.ByReference { }

	public WiaPropertyStorage(){

	}

	public WiaPropertyStorage(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT GetCount(ULONGByReference pulNumProps){
		return (HRESULT)_invokeNativeObject(16, new Object[]{
			this.getPointer(), pulNumProps
		}, HRESULT.class);
	}

	@Override
	public HRESULT Enum(PointerByReference ppenum){
		return (HRESULT)_invokeNativeObject(11, new Object[]{
			this.getPointer(), ppenum
		}, HRESULT.class);
	}

	@Override
	public HRESULT ReadMultiple(int n, PROPSPEC[] propspecs, PROPVARIANT[] propvars){
		HRESULT hr = (HRESULT)_invokeNativeObject(3, new Object[]{
			this.getPointer(), new ULONG(n), propspecs, propvars
		}, HRESULT.class);
		return hr;
	}

	@Override
	public HRESULT WriteMultiple(ULONG n, PROPSPEC[] specs, PROPVARIANT[] values, WiaTypes.PROPID propidNameFirst){
		return (HRESULT)_invokeNativeObject(4, new Object[]{
			this.getPointer(), n, specs, values, propidNameFirst
		}, HRESULT.class);
	}

}
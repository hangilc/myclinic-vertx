package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public class EnumSTATPROPSTG extends Unknown implements IEnumSTATPROPSTG {

	public static class ByReference extends EnumSTATPROPSTG implements Structure.ByReference { }

	public EnumSTATPROPSTG(){

	}

	public EnumSTATPROPSTG(Pointer pointer){
		super(pointer);
	}

	@Override
	public HRESULT Next(ULONG celt, STATPROPSTG[] rgelt, ULONGByReference pceltFetched){
		return (HRESULT)_invokeNativeObject(3, new Object[]{
			this.getPointer(), celt, rgelt, pceltFetched
		}, HRESULT.class);
	}
}

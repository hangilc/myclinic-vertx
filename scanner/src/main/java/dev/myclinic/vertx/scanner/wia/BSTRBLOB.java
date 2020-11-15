package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.ULONG;

import java.util.Arrays;
import java.util.List;

public class BSTRBLOB extends Structure {
	public ULONG cbSize;
	public INT_PTR pData;

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList("cbSize", "pData");
	}
}
package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface MyOle32 extends StdCallLibrary {
	MyOle32 INSTANCE = (MyOle32)Native.loadLibrary("Ole32", MyOle32.class,
		W32APIOptions.DEFAULT_OPTIONS);

	HRESULT FreePropVariantArray(ULONG n, PROPVARIANT[] vars);
}
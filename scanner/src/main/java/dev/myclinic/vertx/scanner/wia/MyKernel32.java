package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface MyKernel32 extends StdCallLibrary {
	MyKernel32 INSTANCE = (MyKernel32)Native.loadLibrary("Kernel32", MyKernel32.class,
		W32APIOptions.DEFAULT_OPTIONS);

	HANDLE GlobalAlloc(int uFlag, SIZE_T dwBytes);
}
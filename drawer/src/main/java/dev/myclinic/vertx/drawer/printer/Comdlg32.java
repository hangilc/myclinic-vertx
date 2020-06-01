package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;


public interface Comdlg32 extends StdCallLibrary, WinUser, WinNT {
	Comdlg32 INSTANCE = (Comdlg32)Native.loadLibrary("Comdlg32", Comdlg32.class, W32APIOptions.DEFAULT_OPTIONS);

	HRESULT PrintDlgEx(PRINTDLGEX pd);
}
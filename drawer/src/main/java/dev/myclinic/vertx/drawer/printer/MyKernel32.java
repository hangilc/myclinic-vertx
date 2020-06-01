package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;

public interface MyKernel32 extends StdCallLibrary, WinUser, WinNT {
    MyKernel32 INSTANCE = (MyKernel32)Native.loadLibrary("kernel32", MyKernel32.class);

    public static final int GHND 			= 0x0042;
    public static final int GMEM_FIXED 		= 0x0000;
    public static final int GMEM_MOVEABLE 	= 0x0002;
    public static final int GMEM_ZEROINIT 	= 0x0040;
    public static final int GPTR 			= 0x004;

    Pointer GlobalLock(HANDLE handle);
    boolean GlobalUnlock(HANDLE handle);
    Pointer GlobalFree(HANDLE handle);
    HANDLE GlobalAlloc(UINT flag, SIZE_T dwBytes);
}

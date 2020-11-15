package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.OSVERSIONINFO;

public class WindowsVersion {

	public static final OSVERSIONINFO osversion = new OSVERSIONINFO();

	static {
		Kernel32.INSTANCE.GetVersionEx(osversion);
	}

	public static boolean isXP(){
		return osversion.dwMajorVersion.intValue() <= 5;
	}
}
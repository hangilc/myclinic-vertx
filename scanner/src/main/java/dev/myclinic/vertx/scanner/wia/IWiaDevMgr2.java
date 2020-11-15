package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WTypes.BSTR;

public interface IWiaDevMgr2 extends IUnknown {
	public static final IID IID_IWiaDevMgr2 = new IID("79C07CF1-CBDD-41ee-8EC3-F00080CADA7A");

	EnumWIA_DEV_INFO EnumDeviceInfo(int flag);
	WiaItem2 CreateDevice(BSTR deviceID);
}


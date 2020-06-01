package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.util.Arrays;
import java.util.List;

public class DEVNAMES extends Structure implements WinDef, WinUser {
	public WORD wDriverOffset;
	public WORD wDeviceOffset;
	public WORD wOutputOffset;
	public WORD wDefault;

	public DEVNAMES(Pointer mem){
		super(mem);
		read();
	}

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList(new String[] {
			"wDriverOffset", "wDeviceOffset", "wOutputOffset", "wDefault"
		});
	}
}
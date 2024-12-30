package dev.myclinic.vertx.drawerprinterwin;

import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinUser;
import java.util.Arrays;

public class XFORM  extends Structure implements WinUser {
    public float eM11;
    public float eM12;
    public float eM21;
    public float eM22;
    public float eDx;
    public float eDy;
  
    @Override
    protected List<String> getFieldOrder() {
		return Arrays.asList(new String[] {
			"eM11", "eM12", "eM21", "eM22", "eDx", "eDy"
		});
    }
    
}

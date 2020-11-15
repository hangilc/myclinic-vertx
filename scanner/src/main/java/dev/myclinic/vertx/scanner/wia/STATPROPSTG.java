package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WTypes.VARTYPE;

import java.util.Arrays;
import java.util.List;

public class STATPROPSTG extends Structure {

	public WString lpwstrName;
	public WiaTypes.PROPID propid;
	public VARTYPE vt;

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList(new String[]{
			"lpwstrName", "propid", "vt"
		});
	}
}

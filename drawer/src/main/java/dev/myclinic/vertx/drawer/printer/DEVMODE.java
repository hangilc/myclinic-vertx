package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.util.Arrays;
import java.util.List;

public class DEVMODE extends Structure implements WinDef, WinUser {
	public static final int CCHDEVICENAME = 32;
	public static final int CCHFORMNAME = 32;

 	public char[] dmDeviceName = new char[CCHDEVICENAME];
 	public WORD dmSpecVersion;
 	public WORD dmDriverVersion;
 	public WORD dmSize;
 	public WORD dmDriverExtra;
 	public DWORD dmFields;
 	public short dmOrientation;
 	public short dmPaperSize;
 	public short dmPaperLength;
 	public short dmPaperWidth;
 	public short dmScale;
 	public short dmCopies;
 	public short dmDefaultSource;
 	public short dmPrintQuality;
 	public short dmColor;
 	public short dmDuplex;
 	public short dmYResolution;
 	public short dmTTOption;
 	public short dmCollate;
 	public char[] dmFormName = new char[CCHFORMNAME];
 	public WORD dmLogPixels;
 	public DWORD dmBitsPerPel;
 	public DWORD dmPelsWidth;
 	public DWORD dmPelsHeight;
 	public DWORD dmDisplayFlags;
 	public DWORD dmDisplayFrequency;
	public DWORD dmICMMethod;
	public DWORD dmICMIntent;
	public DWORD dmMediaType;
	public DWORD dmDitherType;
	public DWORD dmReserved1;
	public DWORD dmReserved2;
	public DWORD dmPanningWidth;
	public DWORD dmPanningHeight;

	public DEVMODE(Pointer mem){
		super(mem);
		ensureAllocated();
		read();
	}

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList(new String[] {
			"dmDeviceName", "dmSpecVersion", "dmDriverVersion", "dmSize", "dmDriverExtra",
 			"dmFields", "dmOrientation", "dmPaperSize", "dmPaperLength", "dmPaperWidth",
 			"dmScale", "dmCopies", "dmDefaultSource", "dmPrintQuality", "dmColor",
 			"dmDuplex", "dmYResolution", "dmTTOption", "dmCollate", "dmFormName",
 			"dmLogPixels", "dmBitsPerPel", "dmPelsWidth", "dmPelsHeight", "dmDisplayFlags",
 			"dmDisplayFrequency", "dmICMMethod", "dmICMIntent", "dmMediaType", "dmDitherType",
			"dmReserved1", "dmReserved2", "dmPanningWidth", "dmPanningHeight"
		});
	}
}
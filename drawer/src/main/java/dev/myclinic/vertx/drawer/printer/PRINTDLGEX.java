package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;

import java.util.Arrays;
import java.util.List;

public class PRINTDLGEX extends Structure implements WinDef, WinUser, WinNT {
	public DWORD lStructSize;
	public HWND hwndOwner;
	public HANDLE hDevMode;
	public HANDLE hDevNames;
	public HDC hDC;
	public DWORD Flags;
	public DWORD Flags2;
	public DWORD ExclusionFlags;
	public DWORD nPageRanges;
	public DWORD nMaxPageRanges;
	public Pointer lpPageRanges;
	public DWORD nMinPage;
	public DWORD nMaxPage;
	public DWORD nCopies;
	public HINSTANCE hInstance;
	public WString lpPrintTemplateName;
	public Pointer lpCallback;
	public DWORD nPropertyPages;
	public Pointer lphPropertyPages;
	public DWORD nStartPage;
	public DWORD dwResultAction;

	public PRINTDLGEX(){
		lStructSize.setValue(size());
	}

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList(new String[] {
			"lStructSize", "hwndOwner", "hDevMode", "hDevNames", "hDC",
			"Flags", "Flags2", "ExclusionFlags", "nPageRanges", "nMaxPageRanges", 
			"lpPageRanges", "nMinPage", "nMaxPage", "nCopies", "hInstance",
			"lpPrintTemplateName", "lpCallback", "nPropertyPages", "lphPropertyPages", "nStartPage",
			"dwResultAction"
		});
	}
}

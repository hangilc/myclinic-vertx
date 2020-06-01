package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.util.Arrays;
import java.util.List;

public class LOGFONT extends Structure implements WinDef, WinUser {
	
	public LONG lfHeight;
	public LONG lfWidth;
	public LONG lfEscapement;
	public LONG lfOrientation;
	public LONG lfWeight;
	public BYTE lfItalic;
	public BYTE lfUnderline;
	public BYTE lfStrikeOut;
	public BYTE lfCharSet;
	public BYTE lfOutPrecision;
	public BYTE lfClipPrecision;
	public BYTE lfQuality;
	public BYTE lfPitchAndFamily;
	public char[] lfFaceName = new char[PrinterConsts.LF_FACESIZE];

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList(new String[] {
			"lfHeight",
			"lfWidth",
			"lfEscapement",
			"lfOrientation",
			"lfWeight",
			"lfItalic",
			"lfUnderline",
			"lfStrikeOut",
			"lfCharSet",
			"lfOutPrecision",
			"lfClipPrecision",
			"lfQuality",
			"lfPitchAndFamily",
			"lfFaceName"
		});
	}
}
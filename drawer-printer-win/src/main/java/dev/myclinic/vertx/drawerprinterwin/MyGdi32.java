package dev.myclinic.vertx.drawerprinterwin;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface MyGdi32 extends StdCallLibrary, WinUser, WinNT {
    MyGdi32 INSTANCE = (MyGdi32)Native.loadLibrary("gdi32", MyGdi32.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean GetTextExtentPoint32(HDC hdc, WString text, int c, SIZE size);
    HFONT CreateFontIndirect(LOGFONT logfont);
    HDC CreateDC(WString driver, WString device, WString output, Pointer devmode);
    boolean DeleteDC(HDC hdc);
    int StartDoc(HDC hdc, DOCINFO docinfo);
    int EndDoc(HDC hdc);
    int AbortDoc(HDC hdc);
    int StartPage(HDC hdc);
    int EndPage(HDC hdc);
    boolean MoveToEx(HDC hdc, int x, int y, POINT point);
    boolean LineTo(HDC hdc, int x, int y);
    boolean TextOut(HDC hdc, int x, int y, WString text, int length);
    Pointer SetTextColor(HDC hdc, int rgb);
    int SetBkMode(HDC hdc, int mode);
    HPEN CreatePen(int style, int width, int rgb);
    HPEN ExtCreatePen(int penStyle, int width, LOGBRUSH brush, int cStyle, DWORD[] pStyle);
    boolean Ellipse(HDC hdc, int left, int top, int right, int bottom);
    HANDLE GetStockObject(int fnObject);
    boolean SetWorldTransform(HDC hdc, XFORM lpxf);
    boolean GetWorldTransform(HDC hdc, XFORM lpxf);
    int SetGraphicsMode(HDC hdc, int iMODE);
}

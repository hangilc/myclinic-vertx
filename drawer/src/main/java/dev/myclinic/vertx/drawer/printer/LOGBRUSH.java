package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;

import java.util.List;

public class LOGBRUSH extends Structure implements WinDef {

    public UINT lbStyle;
    public DWORD lbColor;
    public BaseTSD.ULONG_PTR lbHatch;

    @Override
    protected List<String> getFieldOrder() {
        return List.of("lbStyle", "lbColor", "lbHatch");
    }

}

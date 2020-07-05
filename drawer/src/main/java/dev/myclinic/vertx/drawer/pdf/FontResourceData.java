package dev.myclinic.vertx.drawer.pdf;

import java.util.HashMap;
import java.util.Map;

class FontResourceData {
    public String location;
    public String encoding;

    FontResourceData(String location, String encoding) {
        this.location = location;
        this.encoding = encoding;
    }

    static final Map<String, FontResourceData> fontResourceMap = new HashMap<>();

    static {
        FontResourceData mincho = new FontResourceData("C:\\Windows\\Fonts\\msmincho.ttc,0", "Identity-H");
        fontResourceMap.put("MS Mincho", mincho);
        FontResourceData gothic = new FontResourceData("C:\\Windows\\Fonts\\msgothic.ttc,0", "Identity-H");
        fontResourceMap.put("MS Gothic", gothic);
    }

}

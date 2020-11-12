package dev.myclinic.vertx.drawer.pdf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FontResourceData {
    public String location;
    public String encoding;

    FontResourceData(String location, String encoding) {
        this.location = location;
        this.encoding = encoding;
    }

    private static final Path fontDir;
    static {
        String fontDirEnv = System.getenv("MYCLINIC_FONT_DIR");
        if( fontDirEnv == null ){
            throw new RuntimeException("Cannot find env var: MYCLINIC_FONT_DIR");
        }
        fontDir = Path.of(fontDirEnv);
    }

    private static final Map<String, String> fontMap = new HashMap<>();
    static {
        fontMap.put("MS Mincho", "msmincho.ttc,0:Identity-H");
        fontMap.put("MS Gothic", "msgothic.ttc,0:Identity-H");
    }

    private static FontResourceData getFontResourceData(String name){
        String fontInfo = fontMap.get(name);
        if( fontInfo == null ){
            throw new RuntimeException("Cannot find font with name " + name);
        }
        String[] fontParts = fontInfo.split(":");
        if( fontParts.length != 2 ){
            throw new RuntimeException("Invalid font info: " + fontInfo);
        }
        return new FontResourceData(fontDir.resolve(fontParts[0]).toString(), fontParts[1]);
    }

    static final Map<String, FontResourceData> fontResourceMap = new HashMap<>();

    static {
        for(String name: List.of("MS Mincho", "MS Gothic")){
            FontResourceData data = getFontResourceData(name);
            fontResourceMap.put(name, data);
        }
    }

}

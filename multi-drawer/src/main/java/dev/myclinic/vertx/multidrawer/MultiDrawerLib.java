package dev.myclinic.vertx.multidrawer;

public class MultiDrawerLib {

    public static String adaptFontName(String fontName){
        String fn = fontName.toLowerCase();
        switch(fn){
            case "ms gothic":
            case "gothic":
            case "sans-serif":{
                return "MS Gothic";
            }
            case "ms mincho":
            case "mincho":
            case "serif":{
                return "MS Mincho";
            }
            default:
                return fontName;
        }
    }

}

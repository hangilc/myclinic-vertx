package dev.myclinic.vertx.pdftext;

import dev.myclinic.vertx.drawerform.textform.TextData;

class CmdArgs {

    public String encoding = "UTF-8";
    public TextData data = new TextData();

    CmdArgs(){
        data.fontName = "MS Mincho";
        data.fontSize = 4.0;
        data.leading = 0.0;
    }

    public static CmdArgs parse(String[] args){
        CmdArgs cmdArgs = new CmdArgs();
        int i = 0;
        for(;i<args.length;i++){
            String s = args[i];
            switch(s){
                case "-e": case "--encoding": {
                    cmdArgs.encoding = args[++i];
                    break;
                }
                case "--font-name": {
                    cmdArgs.data.fontName = args[++i];
                    break;
                }
                case "--font-size": {
                    String input = args[++i];
                    try {
                        cmdArgs.data.fontSize = Double.parseDouble(input);
                    } catch(NumberFormatException e){
                        System.err.println("Invalid arg to option --font-size");
                        System.exit(1);
                    }
                    break;
                }
                case "--leading": {
                    String input = args[++i];
                    try {
                        cmdArgs.data.leading = Double.parseDouble(input);
                    } catch(NumberFormatException e){
                        System.err.println("Invalid arg to option --leading");
                        System.exit(1);
                    }
                    break;
                }
                case "-h": {
                    usage();
                    System.exit(0);
                    break;
                }
                default: {
                    System.err.println("Invalid arg: " + s);
                    usage();
                    System.exit(1);
                }
            }
        }
        return cmdArgs;
    }

    static void usage(){
        System.err.println("Usage: pdf-text [options]");
        System.err.println("  -e|--encoding ENCODING         encoding of input (default: UTF-8");
        System.err.println("  --font-name FONT-NAME          font name (default: MS Mincho)");
        System.err.println("  --font-size FONT-NAME          font size (mm) (default: 4.0)");
        System.err.println("  --leading LEADING              inter-line space (mm) (default: 0)");
    }

}

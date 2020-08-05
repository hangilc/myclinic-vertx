package dev.myclinic.vertx.pdfstamp;

import java.util.ArrayList;
import java.util.List;

class CmdArgs {

    String lowerPdf;
    String stampImage;
    double scale = 1.0;
    double xPos = 20;
    double yPos = 40;

    static CmdArgs parse(String[] args){
        CmdArgs cargs = new CmdArgs();
        List<String> rems = new ArrayList<>();
        boolean printHelp = false;
        int i = 0;
        for(;i<args.length;i++){
            String a = args[i];
            if( a.startsWith("-") ){
                switch(a){
                    case "-h": {
                        printHelp = true;
                        break;
                    }
                    case "-s": case "--scale": {
                        String b = args[++i];
                        cargs.scale = Double.parseDouble(b);
                        break;
                    }
                    case "-x": case "--xpos": {
                        String b = args[++i];
                        cargs.xPos = Double.parseDouble(b);
                        break;
                    }
                    case "-y": case "--ypos": {
                        String b = args[++i];
                        cargs.yPos = Double.parseDouble(b);
                        break;
                    }
                    default: {
                        System.err.printf("Unknown option: %s\n", a);
                        usage();
                        System.exit(1);
                        break;
                    }
                }
            } else {
                rems.add(a);
            }
        }
        if( printHelp ){
            usage();
            System.exit(0);
        }
        if( rems.size() != 2 ){
            System.err.println("Invalid number of args.");
            usage();
            System.exit(1);
        }
        cargs.lowerPdf = rems.get(0);
        cargs.stampImage = rems.get(1);
        return cargs;
    }

    private static void usage(){
        System.err.println("Usage: pdf-stamp [options] lower-pdf stamp-image");
        System.err.println("  -s|--scale factor");
        System.err.println("  -x|--xpos x-position     horizontal position of center of the stamp");
        System.err.println("  -y|--ypos y-position     vertical position of center of the stamp");
        System.err.println("  -h                       print help");
    }

}

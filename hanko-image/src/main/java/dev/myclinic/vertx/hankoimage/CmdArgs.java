package dev.myclinic.vertx.hankoimage;

import java.util.ArrayList;
import java.util.List;

class CmdArgs {

    public String inputFile;
    public String outputFile;

    public static CmdArgs parse(String[] args){
        List<String> arglist = new ArrayList<>();
        CmdArgs cargs = new CmdArgs();
        for(int i=0;i<args.length;i++){
            String arg = args[i];
            switch(arg){
                default: {
                    arglist.add(arg);
                    break;
                }
            }
        }
        if( arglist.size() != 2 ){
            System.err.println("Invalid input arguments");
            usage();
            System.exit(1);
        } else {
            cargs.inputFile = arglist.get(0);
            cargs.outputFile = arglist.get(1);
        }
        return cargs;
    }

    static void usage(){
        System.err.println("Usage: hanko-image input-image output-image");
    }

}

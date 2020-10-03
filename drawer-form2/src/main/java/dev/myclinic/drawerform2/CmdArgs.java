package dev.myclinic.drawerform2;

import java.util.ArrayList;
import java.util.List;

class CmdArgs {

    public static class Mark {
        String key;
        String value;

        public Mark(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public boolean pdf;
    public boolean nativeEncoding;
    public List<Mark> marks = new ArrayList<>();


    static void usage(){
        System.err.println("Main [options]");
        System.err.println("options: ");
        System.err.println("--native-encoding");
        System.err.println("--pdf                : output PDF instead of form");
        System.err.println("--mark KEY=VALUE");
    }

    static CmdArgs parse(String[] args){
        CmdArgs cmdArgs = new CmdArgs();
        for(int i = 0; i < args.length; i++){
            String arg = args[i];
            switch(arg){
                case "--pdf": {
                    cmdArgs.pdf = true;
                    break;
                }
                case "--native-encoding": {
                    cmdArgs.nativeEncoding = true;
                    break;
                }
                case "--mark": {
                    String m = args[++i];
                    cmdArgs.marks.add(parseMark(m));
                    break;
                }
                default: {
                    System.err.printf("Unknown argument: %s\n", arg);
                    usage();
                    System.exit(1);
                }
            }
        }
        return cmdArgs;
    }

    private static Mark parseMark(String arg){
        int index = arg.indexOf('=');
        if( index >= 0 ){
            String key = arg.substring(0, index);
            String value = arg.substring(index+1, arg.length());
            return new Mark(key, value);
        } else {
            return new Mark(arg, "");
        }
    }

}

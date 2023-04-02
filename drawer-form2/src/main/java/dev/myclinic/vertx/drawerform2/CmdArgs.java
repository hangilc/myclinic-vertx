package dev.myclinic.vertx.drawerform2;

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
    public String form;


    static void usage(){
        System.err.println("Main [options] form");
        System.err.println("options: ");
        System.err.println("--native-encoding");
        System.err.println("--pdf                : output PDF instead of form");
        System.err.println("--mark KEY=VALUE");
        System.err.println("--help");
        System.err.println("Available forms are: ");
        System.err.println("houmon-kango");
        System.err.println("houmon-kango-rehab");
        System.err.println("refer");
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
                case "--help": {
                    usage();
                    System.exit(0);
                }
                default: {
                    if( cmdArgs.form == null ){
                        cmdArgs.form = arg;
                        break;
                    } else {
                        System.err.printf("Invalid argument: %s\n", arg);
                        usage();
                        System.exit(1);
                    }
                }
            }
        }
        if( cmdArgs.form == null ){
            System.err.println("form name is not specified.");
            usage();
            System.exit(1);
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

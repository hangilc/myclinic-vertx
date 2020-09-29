package dev.myclinic.vertx.rcpt.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class CmdArgs {

    private static Logger logger = LoggerFactory.getLogger(CmdArgs.class);

    String serverUrl;
    int year;
    int month;
    List<Integer> patientIds;
    String gendogakuFile;

    private static void usage(){
        System.err.println("Usage: java -jar rcpt.jar data [options] server-url year month");
        System.err.println("options:");
        System.err.println("  -p123,2211,...    patientIds");
        System.err.println("  -g gendogaku-file");
        System.err.println("  -h                output help");
    }

    static CmdArgs parse(String[] args){
        CmdArgs cmdArgs = new CmdArgs();
        int i = 1;
        for(;i<args.length;i++){
            String arg = args[i];
            if( !arg.startsWith("-") ){
                break;
            }
            if( arg.length() <= 1 ){
                usage();
                System.exit(1);
            }
            char c = arg.charAt(1);
            switch(c){
                case 'p': {
                    cmdArgs.patientIds = parsePatientIds(arg.substring(2));
                    break;
                }
                case 'g': {
                    cmdArgs.gendogakuFile = args[++i];
                    break;
                }
                case 'h': {
                    usage();
                    System.exit(0);
                }
                default: {
                    usage();
                    System.exit(1);
                }
            }
        }
        if( args.length - i != 3 ){
            usage();
            System.exit(1);
        }
        cmdArgs.serverUrl = args[i];
        cmdArgs.year = Integer.parseInt(args[++i]);
        cmdArgs.month = Integer.parseInt(args[++i]);
        return cmdArgs;
    }

    private static List<Integer> parsePatientIds(String str){
        List<Integer> patientIds = new ArrayList<>();
        for(String s: str.split(",")){
            try {
                int patientId = Integer.parseInt(s);
                patientIds.add(patientId);
            } catch(NumberFormatException ex){
                System.err.printf("Invalid pateint Id: %s\n", s);
                System.exit(1);
            }
        }
        if( patientIds.size() == 0 ){
            System.err.println("No patient Ids after -p option.");
            System.exit(1);
        }
        return patientIds;
    }

}

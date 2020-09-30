package dev.myclinic.vertx.rcpt.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class CmdArgs {

    private static Logger logger = LoggerFactory.getLogger(CmdArgs.class);

    String serverUrl;
    int year;
    int month;
    List<Integer> patientIds;
    String gendogakuFile;
    boolean skipGendogakuFile = false;

    private static void usage(){
        System.err.println("Usage: java -jar rcpt.jar data [options] server-url year month");
        System.err.println("options:");
        System.err.println("  -p123,2211,...        : patientIds");
        System.err.println("  -g gendogaku-file     : searches $MYCLINIC_RCPT_DIR if not specified");
        System.err.println("  -G                    : do not use gendogaku file");
        System.err.println("  --server SERVER-URL   : default to $MYCLINIC_SERVICE");
        System.err.println("  -h                    : output help");
    }

    static CmdArgs parse(String[] args) throws Exception {
        CmdArgs cmdArgs = new CmdArgs();
        int i = 1;
        for(;i<args.length;i++){
            String arg = args[i];
            if( arg.equals("--server") ){
                cmdArgs.serverUrl = args[++i];
                i += 1;
                continue;
            }
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
                case 'G': {
                    cmdArgs.skipGendogakuFile = true;
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
        if( args.length - i != 2 ){
            usage();
            System.exit(1);
        }
        if( cmdArgs.serverUrl == null ){
            cmdArgs.serverUrl = System.getenv("MYCLINIC_SERVICE");
            if( cmdArgs.serverUrl == null ){
                System.err.println("Cannot find server URL");
                System.exit(1);
            }
        }
        cmdArgs.year = Integer.parseInt(args[i++]);
        cmdArgs.month = Integer.parseInt(args[i]);
        if( !cmdArgs.skipGendogakuFile && cmdArgs.gendogakuFile == null ){
            cmdArgs.gendogakuFile = probeGendogakuFile();
            if( cmdArgs.gendogakuFile == null ){
                System.err.println("Cannot find gendogaku file.");
                System.err.println("If gendogaku file is not needed, add -G option.");
                System.exit(1);
            }
        }
        if( cmdArgs.gendogakuFile != null ){
            System.err.printf("gendogaku using: %s\n", cmdArgs.gendogakuFile);
        }
        return cmdArgs;
    }

    private static String probeGendogakuFile() throws IOException {
        String dir = System.getenv("MYCLINIC_RCPT_DIR");
        Path dirPath = Path.of(dir);
        if( dir != null ){
            List<String> files = new ArrayList<>();
            for(Path path : Files.newDirectoryStream(dirPath, "gendogaku-*")){
                files.add(path.toFile().getName());
            }
            if( files.size() > 0 ){
                files.sort(Comparator.reverseOrder());
                return dirPath.resolve(files.get(0)).toString();
            }
        }
        return null;
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

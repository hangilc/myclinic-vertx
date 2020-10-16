package dev.myclinic.vertx.master;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class CmdArgs {

    private static void usage(){
        System.err.println("MasterCli command");
        System.err.println("command:");
        System.err.println("  download");
        System.err.println("  update-shinryou [exec=valid-from]");
        System.err.println("  update-shinryou [henkoukubun]");
        System.err.println("             updates current shinryou master with most recently downloaded file");
        System.err.println("  help");
    }

    public static class UpdateShinryouArgs {
        public boolean exec;
        public LocalDate validFrom;
        public boolean henkoukubun;
    }

    public String command;
    public UpdateShinryouArgs updateShinryouArgs;

    public static CmdArgs parse(String[] args){
        if( args.length == 0 ){
            System.err.println("command is missing");
            usage();
            System.exit(1);
        }
        CmdArgs ca = new CmdArgs();
        ca.command = args[0];
        List<String> subargs = Arrays.asList(args).subList(1, args.length);
        switch(ca.command){
            case "download": {
                break;
            }
            case "update-shinryou": {
                ca.updateShinryouArgs = parseUpdateShinryouArgs(subargs);
                break;
            }
            default: {
                System.err.printf("Unknown command: %s\n", ca.command);
                usage();
                System.exit(1);
            }
        }
        return ca;
    }

    private static UpdateShinryouArgs parseUpdateShinryouArgs(List<String> args){
        UpdateShinryouArgs cmdArgs = new UpdateShinryouArgs();
        //noinspection StatementWithEmptyBody
        if( args.size() == 0 ){
            //
        } else if( args.size() == 1 ){
            String a = args.get(0);
            if( a.startsWith("exec=") ){
                cmdArgs.exec = true;
                cmdArgs.validFrom = LocalDate.parse(a.substring("exec=".length()));
            } else if( a.equals("henkoukubun") ){
                cmdArgs.henkoukubun = true;
            } else {
                System.err.printf("Invalid argment to command update-shinryou: %s\n", args.get(0));
                usage();
                System.exit(1);
            }
        } else {
            System.err.println("Too many arguments to commmand update-shinryou");
            usage();
            System.exit(1);
        }
        return cmdArgs;
    }

}

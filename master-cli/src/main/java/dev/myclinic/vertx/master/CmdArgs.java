package dev.myclinic.vertx.master;

class CmdArgs {

    private static void usage(){
        System.err.println("MasterCli command");
        System.err.println("command:");
        System.err.println("  download");
        System.err.println("  help");
    }

    public String command;

    public static CmdArgs parse(String[] args){
        CmdArgs ca = new CmdArgs();
        for(int i=0;i<args.length;i++){
            String arg = args[i];
            switch(arg){
                default: {
                    if( arg.equals("help") ){
                        usage();
                        System.exit(1);
                    }
                    if( ca.command != null ){
                        System.err.println("Too many arguments.");
                        usage();
                        System.exit(1);
                    } else {
                        ca.command = arg;
                    }
                    break;
                }
            }
        }
        if( ca.command == null ){
            System.err.println("command is not specified.");
            usage();
            System.exit(1);
        }
        return ca;
    }

}

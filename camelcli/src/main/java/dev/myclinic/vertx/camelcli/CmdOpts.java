package dev.myclinic.vertx.camelcli;

class CmdOpts {

    public String command;
    public boolean force;

    public static CmdOpts parse(String[] args){
        if( args.length == 0 ){
            throw new RuntimeException("Cannot find command");
        }
        CmdOpts opts = new CmdOpts();
        opts.command = args[0];
        for(int i=1;i<args.length;i++){
            switch(args[i]){
                case "-f": {
                    opts.force = true;
                    break;
                }
                default: {
                    System.err.printf("Unknown option: %s\n", args[i]);
                    System.exit(1);
                }
            }
        }
        return opts;
    }

}

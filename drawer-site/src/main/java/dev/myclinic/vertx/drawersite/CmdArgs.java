package dev.myclinic.vertx.drawersite;

class CmdArgs {

    public boolean isDev;

    public static void usage(){
        System.err.println("usage: drawer-site [OPTIONS]");
        System.err.println("  --dev       serve static files from dev directory");
    }

    public static CmdArgs parse(String[] args){
        CmdArgs cmdArgs = new CmdArgs();
        for(int i=0;i<args.length;i++){
            String arg = args[i];
            switch(arg){
                case "--dev": {
                    cmdArgs.isDev = true;
                    break;
                }
                default: {
                    System.err.println("Invalid arg: " + arg);
                    System.exit(1);
                }
            }
        }
        return cmdArgs;
    }

}

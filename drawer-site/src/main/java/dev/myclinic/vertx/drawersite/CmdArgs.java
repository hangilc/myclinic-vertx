package dev.myclinic.vertx.drawersite;

class CmdArgs {

    public boolean isDev;
    public int port = 48080;
    public String allowedOrigins = null;

    public static void usage(){
        System.err.println("usage: drawer-site [OPTIONS]");
        System.err.println("  --dev                     serve static files from dev directory");
        System.err.println("  --allowed-origins VALUE   allowed origins (comma separated)");
        System.err.println("         If not specified, defaults to MYCLINIC_DRAWER_SITE_ALLOWED_ORIGINS env var.");
        System.err.println("         If neither is specified, default to '*'");
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
                case "--port": {
                    cmdArgs.port = Integer.parseInt(args[++i]);
                    break;
                }
                case "--allowed-origins": {
                    cmdArgs.allowedOrigins = args[++i];
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

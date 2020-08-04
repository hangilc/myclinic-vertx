package dev.myclinic.vertx.pdfstamp;

class CmdArgs {

    String lowerPdf;
    String upperPdf;

    static CmdArgs parse(String[] args){
        CmdArgs cargs = new CmdArgs();
        int i = 0;
        if( i != args.length - 2 ){
            usage();
            System.exit(1);
        }
        cargs.lowerPdf = args[i];
        cargs.upperPdf = args[i+1];
        return cargs;
    }

    private static void usage(){
        System.err.println("Usage: pdf-stamp lower-pdf upper-pdf");
    }

}

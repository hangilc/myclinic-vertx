package dev.myclinic.vertx.cli;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PatientYomi {

    public static void main(String[] args) throws Exception {
        PatientYomi py = new PatientYomi();
        for(int i=0;i<args.length;i++){
            switch(args[i]){
                case "--native-encoding": {
                    py.charset = Charset.defaultCharset();
                    break;
                }
                default: {
                    System.err.println("Invalid argument: " + args[i]);
                }
            }
        }
        py.run();
    }

    private Charset charset = StandardCharsets.UTF_8;

    void run() throws Exception {

    }

}

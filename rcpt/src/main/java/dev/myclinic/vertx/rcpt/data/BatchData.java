package dev.myclinic.vertx.rcpt.data;

import dev.myclinic.vertx.client.Service;
import dev.myclinic.vertx.rcpt.create.Gendogaku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchData {

    private static Logger logger = LoggerFactory.getLogger(BatchData.class);

    private BatchData() {
    }

    public static void run(String[] args) throws Exception {
        CmdArgs cmdArgs = CmdArgs.parse(args);
        String serverUrl = cmdArgs.serverUrl;
        int year = cmdArgs.year;
        int month = cmdArgs.month;
        Service.setServerUrl(serverUrl);
        if( cmdArgs.gendogakuFile != null ){
            Gendogaku.readFromFile(cmdArgs.gendogakuFile);
        } else {
            System.err.println("Is it ok without -g option?");
        }
        Data data = new Data(year, month, cmdArgs.patientIds);
        data.run();
        Service.stop();
    }

}

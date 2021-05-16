package dev.myclinic.vertx.camelcomp.rcpt;

import dev.myclinic.vertx.master.csv.ShinryouMasterCSV;
import dev.myclinic.vertx.master.csv.ZipFileParser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

public class ShinryouMasterContentProcessor implements Processor {

    @Override
    public void process(Exchange ex) throws Exception {
        String data = ex.getIn().getBody(String.class);
        List<ShinryouMasterCSV> recs = new ArrayList<>();
        ZipFileParser.iterShinryouFile(data, recs::add);
        ex.getIn().setBody(recs);
    }
}

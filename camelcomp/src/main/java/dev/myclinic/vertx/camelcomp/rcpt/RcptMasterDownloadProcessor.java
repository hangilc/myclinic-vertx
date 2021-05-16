package dev.myclinic.vertx.camelcomp.rcpt;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.nio.file.Path;

public class RcptMasterDownloadProcessor implements Processor {

    private final Path dstDir;

    public RcptMasterDownloadProcessor(Path dstDir) {
        this.dstDir = dstDir;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String target = exchange.getIn().getBody(String.class);
        switch(target){
            case "shinryou": {
                RcptMasterDownloader.downloadShinryou(dstDir);
                break;
            }
            case "iyakuhin": {
                RcptMasterDownloader.downloadIyakuhin(dstDir);
                break;
            }
            case "kizai": {
                RcptMasterDownloader.downloadKizai(dstDir);
                break;
            }
            case "byoumei": {
                RcptMasterDownloader.downloadByoumei(dstDir);
                break;
            }
            case "shuushokugo": {
                RcptMasterDownloader.downloadShuushokugo(dstDir);
                break;
            }
            default: {
                String msg = String.format("Unknown download target: %s\n", target);
                System.err.println(msg);
                throw new RuntimeException(msg);
            }
        }
    }

}

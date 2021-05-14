package dev.myclinic.vertx.camelcomp.rcpt_master_download;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.support.DefaultEndpoint;

@UriEndpoint(scheme = "rcpt-master-download", syntax = "rcpt-master-download", title = "RcptMasterDownload")
public class RcptMasterDownloadEndpoint extends DefaultEndpoint {

    public RcptMasterDownloadEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new RcptMasterDownloadConsumer(this, processor);
    }

}

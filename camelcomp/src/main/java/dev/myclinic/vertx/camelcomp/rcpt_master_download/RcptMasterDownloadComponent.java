package dev.myclinic.vertx.camelcomp.rcpt_master_download;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

public class RcptMasterDownloadComponent extends DefaultComponent {

    public RcptMasterDownloadComponent() {

    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new RcptMasterDownloadEndpoint();
    }

}

package dev.myclinic.vertx.camelcomp.rcpt_master_download;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

public class RcptMasterDownloadComponent extends DefaultComponent {

    public RcptMasterDownloadComponent() {
        System.out.println("RcptMasterDownloadComponent");
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        System.out.println("createEndpoint " + uri);
        return new RcptMasterDownloadEndpoint(uri, this);
    }

}

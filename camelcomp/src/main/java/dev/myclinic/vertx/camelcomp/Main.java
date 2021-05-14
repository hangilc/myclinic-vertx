package dev.myclinic.vertx.camelcomp;

import dev.myclinic.vertx.camelcomp.rcpt_master_download.RcptMasterDownloadComponent;
import dev.myclinic.vertx.camelcomp.rcpt_master_download.RcptMasterDownloadEndpoint;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {

    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        RcptMasterDownloadComponent comp = new RcptMasterDownloadComponent();
        RcptMasterDownloadEndpoint endpoint = new RcptMasterDownloadEndpoint();
        endpoint.setEndpointUriIfNotSpecified("rcpt-master-download");
        camelContext.addEndpoint("rcpt-master-download", endpoint);
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("rcpt-master-download")
                        .to("stream:out");

            }
        });
        camelContext.start();
        Thread.sleep(5000);
        camelContext.stop();
    }

}

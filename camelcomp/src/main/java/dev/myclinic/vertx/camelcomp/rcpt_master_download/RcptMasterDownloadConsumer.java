package dev.myclinic.vertx.camelcomp.rcpt_master_download;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RcptMasterDownloadConsumer extends DefaultConsumer {

    private static Executor executor = Executors.newFixedThreadPool(1);

    public RcptMasterDownloadConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        System.out.println("rcpt-master-download started.");
        executor.execute(() -> {
            Exchange exchange = getEndpoint().createExchange();
            exchange.getIn().setBody("Hello");
            try {
                getProcessor().process(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
            exchange = getEndpoint().createExchange();
            exchange.getIn().setBody("world");
            try {
                getProcessor().process(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

package dev.myclinic.vertx.camelcomp;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {

    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("timer://myTimer?period=2000")
                        .setBody()
                        .simple("Hello, world fired at ${header.firedTime}.")
                        .to("stream:out");

            }
        });
        camelContext.start();
        Thread.sleep(5000);
        camelContext.stop();
    }

}

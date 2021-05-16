package dev.myclinic.vertx.camelcomp;

import dev.myclinic.vertx.camelcomp.file.FileName;
import dev.myclinic.vertx.camelcomp.rcpt.RcptMasterDownloadProcessor;
import dev.myclinic.vertx.camelcomp.rcpt.RcptMasterDownloader;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) throws Exception {
        CmdOpts cmdOpts = CmdOpts.parse(args);
        switch(cmdOpts.command){
            case "download-masters": {
                Path dstDir = Path.of("./work/masters");
                if( Files.exists(dstDir) ){
                    if( cmdOpts.force ){
                        String saveFile = FileName.createTimestampedFileName("masters-", "");
                        Path moveDir = Path.of("./work/", saveFile);
                        Files.move(dstDir, moveDir);
                    } else {
                        System.err.printf("%s already exists.\n", dstDir);
                        System.exit(1);
                    }
                }
                Files.createDirectories(dstDir);
                CamelContext context = new DefaultCamelContext();
                CompletableFuture<Void> control = new CompletableFuture<>();
                context.addRoutes(new RouteBuilder(){
                    @Override
                    public void configure() throws Exception {
                        from("direct:start-rcpt-master-download")
                                .process(ex -> {
                                    String targets = ex.getIn().getBody(String.class);
                                    if( targets.equals("all") ){
                                        String allTargets = "shinryou,iyakuhin,kizai,byoumei,shuushokugo";
                                        ex.getIn().setBody(allTargets);
                                    }
                                })
                                .split(body(), ",").parallelProcessing()
                                    .process(new RcptMasterDownloadProcessor(dstDir))
                                .end()
                                .to("stream:out")
                                .process(ex -> control.complete(null));
                    }
                });
                context.start();
                context.createFluentProducerTemplate()
                        .to("direct:start-rcpt-master-download")
                        .withBody("all")
                        .send();
                control.whenComplete((v, e) -> context.stop());
                break;
            }
            default: {
                System.err.printf("Unknown command: %s\n", args[0]);
                System.exit(1);
            }
        }
    }

}

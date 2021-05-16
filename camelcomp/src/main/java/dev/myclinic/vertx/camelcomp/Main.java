package dev.myclinic.vertx.camelcomp;

import dev.myclinic.vertx.camelcomp.file.FileName;
import dev.myclinic.vertx.camelcomp.rcpt.RcptMasterDownloadProcessor;
import dev.myclinic.vertx.camelcomp.rcpt.ShinryouMasterContentProcessor;
import dev.myclinic.vertx.master.csv.MasterHandler;
import dev.myclinic.vertx.master.csv.ShinryouMasterCSV;
import dev.myclinic.vertx.master.db.DB;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
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
                control.join();
                context.stop();
                break;
            }
            case "2021-04-kansenshou-kasan": {
                CamelContext context = new DefaultCamelContext();
                CompletableFuture<Void> control = new CompletableFuture<>();
                Connection conn = DB.openConnection();
                context.addRoutes(new RouteBuilder(){
                    @Override
                    public void configure() throws Exception {
                        from("file:./work/masters?noop=true&fileName=s.zip")
                                .unmarshal().zipFile()
                                .convertBodyTo(String.class, "MS932")
                                .process(new ShinryouMasterContentProcessor())
                                .split(body())
                                    .process(ex -> {
                                        ShinryouMasterCSV rec = ex.getIn().getBody(ShinryouMasterCSV.class);
                                        if( rec.name.contains("感染症対策実施加算") ){
                                            MasterHandler.enterShinryouMaster(conn, rec, "2021-04-01");
                                        }
                                    })
                                .end()
                                .process(ex -> control.complete(null));
                    }
                });
                context.start();
                control.join();
                conn.close();
                context.stop();
                break;
            }
            default: {
                System.err.printf("Unknown command: %s\n", args[0]);
                System.exit(1);
            }
        }
    }

}

package dev.myclinic.vertx.rcpt.create;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dev.myclinic.vertx.client.Service;
import dev.myclinic.vertx.rcpt.Common;
import dev.myclinic.vertx.rcpt.create.bill.Bill;
import dev.myclinic.vertx.rcpt.create.bill.HoukatsuKensaRevision;
import dev.myclinic.vertx.rcpt.create.input.Rcpt;
import dev.myclinic.vertx.rcpt.create.input.Seikyuu;
import dev.myclinic.vertx.rcpt.create.output.Output;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedMap;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Comparator;

public class Create {

    private static Logger logger = LoggerFactory.getLogger(Create.class);

    public static void run(String serverUrl, String xmlDataFile, PrintStream printStream){
        Service.setServerUrl(serverUrl);
        try (FileInputStream ins = new FileInputStream(xmlDataFile)) {
            XmlMapper mapper = new XmlMapper();
            Rcpt rcpt = mapper.readValue(ins, Rcpt.class);
            rcpt.seikyuuList.sort(seikyuuComparator());
            LocalDate at = rcpt.getDate(1);
            ResolvedMap resolvedMap = dev.myclinic.vertx.rcpt.Common.getMasterMaps(at);
            ResolvedShinryouMap shinryouMasterMap = resolvedMap.shinryouMap;
            HoukatsuKensaRevision houkatsuKensaRevision = HoukatsuKensaRevision.load();
            HoukatsuKensaRevision.Revision revision = houkatsuKensaRevision.findRevision(at);
            Output output = new Output(printStream);
            Bill bill = new Bill(rcpt, output, shinryouMasterMap, revision);
            bill.run();
        } catch (Exception ex) {
            logger.error("Failed to run create.", ex);
            System.exit(1);
        } finally {
            Service.stop();
        }
    }

    private static Comparator<Seikyuu> seikyuuComparator() {
        Comparator<Seikyuu> comp = Comparator.comparing(Seikyuu::getRankTag);
        return comp.thenComparing(s -> s.patientId);
    }


}

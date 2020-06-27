package dev.myclinic.vertx.drawerform.referform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.drawer.printer.DrawerPrinter;
import dev.myclinic.vertx.drawerform.Paper;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        ReferData data = new ReferData();
        ReferForm form = new ReferForm();
        List<Op> ops = form.render(data);
        Paper paper = Paper.getPaperByName("A4");
        PdfPrinter printer = new PdfPrinter(paper.getWidth(), paper.getHeight());
        printer.print(List.of(ops), "work/refer.pdf");
    }

    private void dumpOps(List<Op> ops) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        mapper.registerModule(module);

        System.out.println(mapper.writeValueAsString(ops));
    }

}

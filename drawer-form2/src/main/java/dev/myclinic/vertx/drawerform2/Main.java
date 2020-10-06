package dev.myclinic.vertx.drawerform2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawerform2.forms.HoumonKango;
import dev.myclinic.vertx.drawer.*;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        module.addDeserializer(Op.class, new JacksonOpDeserializer());
        mapper.registerModule(module);
    }

    public static void main(String[] args) throws Exception {
        CmdArgs cmdArgs = CmdArgs.parse(args);
        HoumonKango creator = new HoumonKango();
        Form form = creator.createForm();
        if (cmdArgs.pdf) {
            outputPdf(form, cmdArgs.marks);
        } else {
            if (cmdArgs.nativeEncoding) {
                String s = mapper.writeValueAsString(form);
                System.out.printf("%s", s);
            } else {
                mapper.writeValue(System.out, form);
            }
        }
    }

    private static void outputPdf(Form form, List<CmdArgs.Mark> marks) throws Exception {
        DrawerCompiler c = new DrawerCompiler();
        c.importOps(form.setup);
        c.clearOps();
        List<List<Op>> pages = new ArrayList<>();
        for(Page page: form.pages){
            c.importOps(page.ops);
            pages.add(c.getOps());
            c.clearOps();
        }
        PaperSize paperSize = PaperSize.resolvePaperSize(form.paper);
        PdfPrinter pdfPrinter = new PdfPrinter(paperSize);
        pdfPrinter.print(form.setup, pages, System.out);
    }

}

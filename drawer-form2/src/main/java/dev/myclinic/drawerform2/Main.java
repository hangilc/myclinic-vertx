package dev.myclinic.drawerform2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.drawerform2.houmonkango.HoumonKango;
import dev.myclinic.vertx.drawer.*;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;

import java.util.List;

import static dev.myclinic.vertx.drawer.Render.Form;

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
        PaperSize paperSize = PaperSize.resolvePaperSize(form.page);
        PdfPrinter pdfPrinter = new PdfPrinter(paperSize);
        if (marks.size() > 0) {
            Render render = new Render(form);
            for (CmdArgs.Mark m : marks) {
                render.add(m.key, m.value);
            }
            pdfPrinter.print(List.of(render.getOps()), System.out);
        } else {
            pdfPrinter.print(List.of(form.form), System.out);
        }
    }

}

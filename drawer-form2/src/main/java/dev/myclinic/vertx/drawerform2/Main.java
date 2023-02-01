package dev.myclinic.vertx.drawerform2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawerform2.forms.CovidVac2ndShot;
import dev.myclinic.vertx.drawerform2.forms.HoumonKango;
import dev.myclinic.vertx.drawerform2.forms.HoumonKangoRehab;
import dev.myclinic.vertx.drawer.*;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.drawerform2.forms.Refer;

import java.util.ArrayList;
import java.util.HashMap;
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
        Form form = null;
        switch(cmdArgs.form){
            case "houmon-kango": {
                HoumonKango houmonKango = new HoumonKango();
                form = houmonKango.createForm();
                break;
            }
            case "houmon-kango-rehab": {
                HoumonKangoRehab houmonKangoRehab = new HoumonKangoRehab();
                form = houmonKangoRehab.createForm();
                break;
            }
            case "refer": {
                Refer refer = new Refer();
                form = refer.createForm();
                break;
            }
            case "covid-vac-2nd-shot": {
                form = CovidVac2ndShot.createForm();
                break;
            }
            default: {
                System.err.printf("Unknown form name: %s\n", cmdArgs.form);
                System.exit(1);
            }
        }
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
        List<PdfPrinter.FormPageData> pageDataList = new ArrayList<>();
        for(int i=0;i<form.pages.size();i++){
            Page page = form.pages.get(i);
            PdfPrinter.FormPageData pageData = new PdfPrinter.FormPageData();
            pageData.pageId = i;
            pageData.markTexts = new HashMap<>();
            pageData.customRenderers = new HashMap<>();
            for(CmdArgs.Mark mark: marks){
                String key = mark.key;
                if( page.marks.containsKey(key) ){
                    pageData.markTexts.put(key, mark.value);
                }
            }
            pageDataList.add(pageData);
        }
        PdfPrinter pdfPrinter = new PdfPrinter(form.paper);
        pdfPrinter.print(form, pageDataList, System.out);
    }

}

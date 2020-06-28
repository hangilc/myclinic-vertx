package dev.myclinic.vertx.drawerform.referform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.drawerform.Paper;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        ReferData data = new ReferData();
        data.title = "紹介状";
        data.referHospital = "県立某市中央病院";
        data.referDoctor = "田中一郎 先生";
        data.patientName = "患者： 鈴木二郎 様";
        data.patientInfo = "昭和32年1月1日生　63才 男性";
        data.diagnosis = "診断： 高血圧、糖尿病";
        data.issueDate = "令和2年6月28日";
        data.clinicPostalCode = "〒123-4567";
        data.clinicAddress = "東京都";
        data.clinicPhone = "電話 03-1234-5678";
        data.clinicFax = "FAX 03-1234-5678";
        data.clinicName = "内科診療所";
        data.doctorName = "診療太郎";
        data.content = "いつも大変お世話になっております。\n高血圧、糖尿病にて当院に通院されている方ですが、";
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

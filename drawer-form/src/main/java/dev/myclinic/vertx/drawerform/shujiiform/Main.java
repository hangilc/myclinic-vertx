package dev.myclinic.vertx.drawerform.shujiiform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.drawer.JacksonOpDeserializer;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.printer.DrawerPrinter;
import dev.myclinic.vertx.drawerform.Box;
import dev.myclinic.vertx.drawerform.FormCompiler;
import dev.myclinic.vertx.drawerform.Paper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main2(String[] args) throws Exception {
        FormCompiler c = new FormCompiler();
//        c.setScale(1.101, 1.071);
        c.setOffsetX(-2.5);
        c.setOffsetY(-2.5);
        c.createPen("regular", 0, 0, 0, 0.1);
        c.setPen("regular");
        c.box(new Box(0, 0, Paper.A4.getWidth(), Paper.A4.getHeight()).inset(10));
        List<Op> ops = c.getOps();
        DrawerPrinter printer = new DrawerPrinter();
        printer.print(ops);
    }

    public static void main(String[] args) throws Exception {
        ShujiiData data = new ShujiiData();
        data.doctorName = "診療太郎";
        data.clinicName = "東京診療所";
        data.clinicAddress = "東京０３";
        data.phone = "03-1234-5678";
        data.fax = "03-9876-5432";
        data.detail = "平成１７年より、高血圧、高脂血症にて当院に通院している。";

        ShujiiForm form = new ShujiiForm();
        FormCompiler c = form.getCompiler();
        c.setOffsetX(-2.5);
        c.setOffsetY(-2.5);
        List<Op> ops = form.render(data);
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Op.class, new JacksonOpSerializer());
        mapper.registerModule(module);
        List<List<Op>> pages = List.of(ops);
        DrawerPrinter printer = new DrawerPrinter();
        printer.print(ops);
//        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pages);
//        if( args.length > 0 ){
//            Files.write(Path.of(args[0]), json.getBytes(StandardCharsets.UTF_8));
//        } else {
//            System.out.println(json);
//        }
    }

}

package dev.myclinic.drawerform2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.drawerform2.houmonkango.HoumonKango;
import dev.myclinic.vertx.drawer.JacksonOpDeserializer;
import dev.myclinic.vertx.drawer.JacksonOpSerializer;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.Render;

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
        HoumonKango creator = new HoumonKango();
        Render.Form form = creator.createForm();
        mapper.writeValue(System.out, form);
    }

}

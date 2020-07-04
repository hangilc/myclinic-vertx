package dev.myclinic.vertx.drawer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hangil on 2017/05/14.
 */
public class JacksonOpDeserializer extends StdDeserializer<Op> {

    public JacksonOpDeserializer(){
        this(Op.class);
    }

    public JacksonOpDeserializer(Class<?> c){
        super(c);
    }

    private OpCodeMapper opCodeMapper = new OpCodeMapper();

    @Override
    public Op deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        JsonNode identNode = node.get(0);
        if( identNode == null ){
            throw new RuntimeException("cannot find ident node for drawer op");
        }
        String ident = identNode.asText();
        switch(opCodeMapper.map(ident)){
            case MoveTo: {
                double x = node.get(1).asDouble();
                double y = node.get(2).asDouble();
                return new OpMoveTo(x, y);
            }
            case LineTo: {
                double x = node.get(1).asDouble();
                double y = node.get(2).asDouble();
                return new OpLineTo(x, y);
            }
            case CreateFont: {
                String name = node.get(1).asText();
                String fontName = node.get(2).asText();
                double size = node.get(3).asDouble();
                int weight = node.get(4).asInt();
                boolean italic = node.get(5).asInt() != 0;
                return new OpCreateFont(name, fontName, size, weight, italic);
            }
            case SetFont: {
                String name = node.get(1).asText();
                return new OpSetFont(name);
            }
            case DrawChars: {
                String chars = node.get(1).asText();
                List<Double> xs = new ArrayList<>();
                for(JsonNode xnode: node.get(2)){
                    xs.add(xnode.asDouble());
                }
                List<Double> ys = new ArrayList<>();
                for(JsonNode ynode: node.get(3)){
                    ys.add(ynode.asDouble());
                }
                return new OpDrawChars(chars, xs, ys);
            }
            case SetTextColor: {
                int r = node.get(1).asInt();
                int g = node.get(2).asInt();
                int b = node.get(3).asInt();
                return new OpSetTextColor(r, g, b);
            }
            case CreatePen: {
                String name = node.get(1).asText();
                int r = node.get(2).asInt();
                int g = node.get(3).asInt();
                int b = node.get(4).asInt();
                double width = node.get(5).asDouble();
                List<Double> penStyle = new ArrayList<>();
                for(JsonNode n: node.get(6)){
                    penStyle.add(n.asDouble());
                }
                return new OpCreatePen(name, r, g, b, width, penStyle);
            }
            case SetPen: {
                String name = node.get(1).asText();
                return new OpSetPen(name);
            }
            case Circle: {
                double cx = node.get(1).asDouble();
                double cy = node.get(2).asDouble();
                double r = node.get(3).asDouble();
                return new OpCircle(cx, cy, r);
            }
            default: throw new RuntimeException("unknown op code of drawer: " + ident);
        }
    }
}

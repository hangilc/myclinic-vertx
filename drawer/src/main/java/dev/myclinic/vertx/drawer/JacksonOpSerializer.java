package dev.myclinic.vertx.drawer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.List;

public class JacksonOpSerializer extends StdSerializer<Op> {

    public JacksonOpSerializer(){
        super(Op.class);
    }

    public JacksonOpSerializer(Class<Op> t){
        super(t);
    }

    @Override
    public void serialize(Op op, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        gen.writeStartArray();
        gen.writeString(op.getOpCode().getIdent());
        switch(op.getOpCode()){
            case MoveTo: {
                OpMoveTo opMoveTo = (OpMoveTo)op;
                gen.writeNumber(opMoveTo.getX());
                gen.writeNumber(opMoveTo.getY());
                break;
            }
            case LineTo: {
                OpLineTo opLineTo = (OpLineTo)op;
                gen.writeNumber(opLineTo.getX());
                gen.writeNumber(opLineTo.getY());
                break;
            }
            case CreateFont: {
                OpCreateFont opCreateFont = (OpCreateFont)op;
                gen.writeString(opCreateFont.getName());
                gen.writeString(opCreateFont.getFontName());
                gen.writeNumber(opCreateFont.getSize());
                gen.writeNumber(opCreateFont.getWeight());
                gen.writeNumber(opCreateFont.isItalic() ? 1 : 0);
                break;
            }
            case SetFont: {
                OpSetFont opSetFont = (OpSetFont)op;
                gen.writeString(opSetFont.getName());
                break;
            }
            case DrawChars: {
                OpDrawChars opDrawChars = (OpDrawChars)op;
                gen.writeString(opDrawChars.getChars());
                gen.writeStartArray();
                for(double x: opDrawChars.getXs()){
                    gen.writeNumber(x);
                }
                gen.writeEndArray();
                gen.writeStartArray();
                for(double y: opDrawChars.getYs()){
                    gen.writeNumber(y);
                }
                gen.writeEndArray();
                break;
            }
            case SetTextColor: {
                OpSetTextColor opSetTextColor = (OpSetTextColor)op;
                gen.writeNumber(opSetTextColor.getR());
                gen.writeNumber(opSetTextColor.getG());
                gen.writeNumber(opSetTextColor.getB());
                break;
            }
            case CreatePen: {
                OpCreatePen opCreatePen = (OpCreatePen)op;
                gen.writeString(opCreatePen.getName());
                gen.writeNumber(opCreatePen.getR());
                gen.writeNumber(opCreatePen.getG());
                gen.writeNumber(opCreatePen.getB());
                gen.writeNumber(opCreatePen.getWidth());
                double[] penStyle = opCreatePen.getPenStyle().stream().mapToDouble(d -> d).toArray();
                gen.writeArray(penStyle, 0, penStyle.length);
                break;
            }
            case SetPen: {
                OpSetPen opSetPen = (OpSetPen)op;
                gen.writeString(opSetPen.getName());
                break;
            }
            case Circle: {
                OpCircle opCircle = (OpCircle)op;
                gen.writeNumber(opCircle.getCx());
                gen.writeNumber(opCircle.getCy());
                gen.writeNumber(opCircle.getR());
                break;
            }
            default: throw new RuntimeException("unknown drawer op: " + op);
        }
        gen.writeEndArray();
    }
}

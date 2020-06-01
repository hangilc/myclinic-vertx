package dev.myclinic.vertx.drawer;

public enum OpCode {

    MoveTo("move_to"),
    LineTo("line_to"),
    CreateFont("create_font"),
    SetFont("set_font"),
    DrawChars("draw_chars"),
    SetTextColor("set_text_color"),
    CreatePen("create_pen"),
    SetPen("set_pen"),
    Circle("circle");

    private String ident;

    OpCode(String ident){
        this.ident = ident;
    }

    String getIdent(){
        return ident;
    }

    @Override
    public String toString(){
        return "OpCode[" +
                    "ident=" + ident +
                "]";
    }

 }
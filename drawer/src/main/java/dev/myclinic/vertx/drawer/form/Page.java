package dev.myclinic.vertx.drawer.form;

import dev.myclinic.vertx.drawer.Op;

import java.util.List;
import java.util.Map;

public class Page {

    public List<Op> ops;
    public Map<String, Rect> marks;
    public Map<String, String> hints;
    public List<String> descriptions;
    public String name;

}

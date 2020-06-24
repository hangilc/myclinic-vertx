package dev.myclinic.vertx.drawerform;

import dev.myclinic.vertx.drawer.Op;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormSpec {

    public Paper paper = Paper.A4;
    public Map<String, Box> marks = new HashMap<>();
    public Map<String, String> markHints = new HashMap<>();
    public List<List<Op>> forms = new ArrayList<>();

}

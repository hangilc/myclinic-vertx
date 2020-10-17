package dev.myclinic.vertx.multidrawer.seal8x3;

import java.util.ArrayList;
import java.util.List;

public class Seal8x3Data {

    public int startRow = 1;  // 1-based
    public int startColumn = 1; // 1-based
    public List<List<String>> labels = new ArrayList<>();
    public double leftMargin = 6;
    public double rightMargin = 6;
    public double topMargin = 12.9;
    public double bottomMargin = 12.9;
    public double padding = 5;
    public double shiftX = -1;
    public double shiftY = 0;
    public String fontName = "serif";
    public double fontSize = 3.8;

}

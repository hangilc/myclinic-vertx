package dev.myclinic.vertx.multidrawer.seal8x3;

import dev.myclinic.vertx.drawer.Box;
import dev.myclinic.vertx.drawer.DrawerCompiler;
import dev.myclinic.vertx.drawer.Op;
import dev.myclinic.vertx.drawer.PaperSize;
import dev.myclinic.vertx.multidrawer.DataDrawer;
import dev.myclinic.vertx.multidrawer.MultiDrawerLib;

import java.util.ArrayList;
import java.util.List;

public class Seal8x3Drawer implements DataDrawer<Seal8x3Data> {

    @Override
    public List<List<Op>> draw(Seal8x3Data data) {
        DrawerCompiler c = new DrawerCompiler();
        List<List<Op>> pages = new ArrayList<>();
        Box box = new Box(PaperSize.A4).inset(data.leftMargin, data.topMargin,
                data.rightMargin, data.bottomMargin);
        int row = data.startRow;
        if( !(row >= 1 && row <= 8) ){
            throw new RuntimeException("Invalid start row (should be 1 - 8)");
        }
        int col = data.startColumn;
        if( !(col >= 1 && col <= 3) ){
            throw new RuntimeException("Invalid start col (should be 1 - 3)");
        }
        startPage(c, data);
        for(List<String> content: data.labels){
            Box cell = getCell(box, row, col);
            cell = cell.inset(data.padding);
            cell = cell.shift(data.shiftX, data.shiftY);
            c.multilineText(content, cell, DrawerCompiler.HAlign.Left,
                    DrawerCompiler.VAlign.Top, 0);
            col += 1;
            if( col > 3 ){
                row += 1;
                col = 1;
            }
            if( row > 8 ){
                pages.add(c.getOps());
                startPage(c, data);
                row = 1;
            }
        }
        pages.add(c.getOps());
        return pages;
    }

    private void startPage(DrawerCompiler c, Seal8x3Data data){
        String fontName = MultiDrawerLib.adaptFontName(data.fontName);
        c.clearOps();
        c.createFont("default", fontName, data.fontSize);
        c.setFont("default");
    }

    private Box getCell(Box box, int row, int col){
        double cx = box.getWidth() / 3.0;
        double cy = box.getHeight() / 8.0;
        return new Box(
                box.getLeft() + cx * (col - 1),
                box.getTop() + cy * (row - 1),
                box.getLeft() + cx * col,
                box.getTop() + cy * row
        );
    }
}

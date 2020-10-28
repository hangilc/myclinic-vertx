package dev.myclinic.vertx.drawer;

import dev.myclinic.vertx.drawer.Op;

import java.util.ArrayList;
import java.util.List;

public class PrintRequest {

    public List<Op> setup;
    public List<List<Op>> pages;

    public List<List<Op>> convertToPages(){
        if( setup != null && setup.size() > 0 ){
            if( pages.size() == 0 ){
                return List.of(setup);
            } else {
                List<List<Op>> result = new ArrayList<>();
                List<Op> firstPage = new ArrayList<>();
                firstPage.addAll(setup);
                firstPage.addAll(pages.get(0));
                result.add(firstPage);
                result.addAll(pages.subList(1, pages.size()));
                return result;
            }
        } else {
            return pages;
        }
    }
}

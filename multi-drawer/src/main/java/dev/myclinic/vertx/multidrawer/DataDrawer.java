package dev.myclinic.vertx.multidrawer;

import dev.myclinic.vertx.drawer.Op;

import java.util.List;

public interface DataDrawer<T> {
    List<List<Op>> draw(T data);
}

package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;

public interface Multi {

    Box render(FormCompiler c, Box box);

    default public MarkedMulti mark(String key){
        return new MarkedMulti(this, key);
    }

}

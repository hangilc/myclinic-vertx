package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public interface Multi {

    Box render(FormCompiler c, Box box, VAlign valign);

    default public MarkedMulti mark(String key){
        return new MarkedMulti(this, key);
    }

}

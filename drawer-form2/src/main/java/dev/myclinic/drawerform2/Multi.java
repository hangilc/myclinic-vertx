package dev.myclinic.drawerform2;

import dev.myclinic.vertx.drawer.Box;
import static dev.myclinic.vertx.drawer.DrawerCompiler.VAlign;

public interface Multi {

    Box render(FormCompiler c, Box box, VAlign valign);

    default MarkedMulti mark(String key, String description){
        return new MarkedMulti(this, key, description);
    }

}

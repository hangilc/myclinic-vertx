package dev.myclinic.vertx.rcpt.check;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class CheckDuplicates extends CheckBase {

    //private static Logger logger = LoggerFactory.getLogger(CheckDuplicates.class);
    CheckDuplicates(Scope scope) {
        super(scope);
    }

    void check(){
        Set<Integer> excludes = Set.of(getShinryouMaster().非特異的ＩｇＥ);
        forEachVisit(visit -> {
            Map<Integer, Integer> counts = new HashMap<>(); // shinryoucode -> count
            Map<Integer, String> nameMap = new HashMap<>(); // shinryoucode -> name
            forEachShinryou(visit, shinryou -> {
                int shinryoucode = shinryou.master.shinryoucode;
                if( excludes.contains(shinryoucode) ){
                    return;
                }
                counts.merge(shinryou.master.shinryoucode, 1, (a, b) -> a + b);
                nameMap.put(shinryou.master.shinryoucode, shinryou.master.name);
            });
            counts.forEach((k, c) -> {
                if( c > 1 ){
                    String name = nameMap.get(k);
                    error(name + "重複", messageForRemoveExtra(name, c, 1),
                            () -> removeExtraShinryouMaster(visit, k, 1)
                    );
                }
            });
        });
    }

}

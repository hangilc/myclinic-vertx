package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;

import java.util.HashMap;
import java.util.Map;

class ShinryouAliasMap {

    //private static Logger logger = LoggerFactory.getLogger(ShinryouAliasMap.class);

    private ShinryouAliasMap() {

    }

    static Map<Integer, String> create(ResolvedShinryouMap resolvedShinryouMap){
        Map<Integer, String> map = new HashMap<>();
//        map.put(resolvedShinryouMap.薬剤情報提供, "（薬情）");
//        map.put(resolvedShinryouMap.特定疾患管理, "（特）");
//        map.put(resolvedShinryouMap.特定疾患処方, "（特処）");
//        map.put(resolvedShinryouMap.特定疾患処方管理加算処方せん料, "（特処）");
//        map.put(resolvedShinryouMap.長期処方, "（特処長）");
//        map.put(resolvedShinryouMap.長期投薬加算処方せん料, "（特処長）");
        return map;
    }

}

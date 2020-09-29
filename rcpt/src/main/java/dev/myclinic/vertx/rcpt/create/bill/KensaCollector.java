package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.rcpt.create.input.Shinryou;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedShinryouMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class KensaCollector {

    private static Logger logger = LoggerFactory.getLogger(KensaCollector.class);
    private ResolvedShinryouMap resolvedShinryouMap;
    private Map<HoukatsuKensaKind, List<Shinryou>> houkatsuMap = new LinkedHashMap<>();
    private List<Shinryou> handanryouList = new ArrayList<>();
    private List<Shinryou> shinryouList = new ArrayList<>();

    KensaCollector(ResolvedShinryouMap resolvedShinryouMap) {
        this.resolvedShinryouMap = resolvedShinryouMap;
    }

    void add(Shinryou shinryou){
        HoukatsuKensaKind kind = HoukatsuKensaKind.fromCode(shinryou.houkatsuKensa);
        if( kind != null && kind != HoukatsuKensaKind.NONE ){
            addHoukatsuKensa(kind, shinryou);
        } else if( isHandanryou(shinryou.getShinryoucode()) ) {
            handanryouList.add(shinryou);
        } else {
            shinryouList.add(shinryou);
        }
    }

    public Map<HoukatsuKensaKind, List<Shinryou>> getHoukatsuMap() {
        return houkatsuMap;
    }

    public List<Shinryou> getHandanryouList() {
        return handanryouList;
    }

    public List<Shinryou> getShinryouList() {
        return shinryouList;
    }

    private void addHoukatsuKensa(HoukatsuKensaKind kind, Shinryou shinryou){
        if( houkatsuMap.containsKey(kind) ){
            houkatsuMap.get(kind).add(shinryou);
        } else {
            List<Shinryou> list = new ArrayList<>();
            list.add(shinryou);
            houkatsuMap.put(kind, list);
        }
    }

    private boolean isHandanryou(int shinryoucode){
        ResolvedShinryouMap map = resolvedShinryouMap;
        return shinryoucode == map.尿便検査判断料 ||
                shinryoucode == map.血液検査判断料 ||
                shinryoucode == map.生化Ⅰ判断料 ||
                shinryoucode == map.生化Ⅱ判断料 ||
                shinryoucode == map.免疫検査判断料 ||
                shinryoucode == map.微生物検査判断料 ||
                shinryoucode == map.病理判断料;
    }


}

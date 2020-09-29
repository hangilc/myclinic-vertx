package dev.myclinic.vertx.rcpt.unit;

import dev.myclinic.vertx.consts.DrugCategory;
import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.dto.DrugFullDTO;
import dev.myclinic.vertx.dto.ShinryouFullDTO;
import dev.myclinic.vertx.dto.VisitFull2DTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RcptUnit {

    private static Logger logger = LoggerFactory.getLogger(RcptUnit.class);
    private Map<Integer, SimpleShinryouItem> simpleShinryouMap = new HashMap<>();
    private List<HoukatsuKensaItem> houkatsuKensaItems = new ArrayList<>();
    private List<NaifukuItem> naifukuItems = new ArrayList<>();
    private List<TonpukuItem> tonpukuItems = new ArrayList<>();
    private List<GaiyouItem> gaiyouItems = new ArrayList<>();

    private static <T extends Extendable<T>> void extendList(List<T> list, T item){
        for(T curr: list){
            if( curr.isExtendableWith(item) ){
                curr.extendWith(item);
                return;
            }
        }
        list.add(item);
    }

    private static <T extends Mergeable<T>> void mergeListOne(List<T> list, T item){
        for(T dst: list){
            if( dst.isMergeableWith(item) ){
                dst.incCount(item.getCount());
                return;
            }
        }
        list.add(item);
    }

    private static <T extends Mergeable<T> & Countable> void mergeList(List<T> dst, List<T> src){
        src.forEach(s -> mergeListOne(dst, s));
    }

    private static <K, T extends Countable> void mergeMapOne(Map<K, T> map, K key, T item){
        T curr = map.get(key);
        if( curr == null ){
            map.put(key, item);
        } else {
            curr.incCount(item.getCount());
        }
    }

    private static <K, T extends Countable> void mergeMap(Map<K, T> dst, Map<K, T> src){
        src.forEach((k, v) -> mergeMapOne(dst, k, v));
    }

    RcptUnit() {

    }

    RcptUnit(VisitFull2DTO visit){
        visit.shinryouList.forEach(this::addShinryou);
        visit.drugs.forEach(this::addDrug);
    }

    void merge(RcptUnit arg){
        mergeMap(simpleShinryouMap, arg.simpleShinryouMap);
        mergeList(houkatsuKensaItems, arg.houkatsuKensaItems);
        mergeList(naifukuItems, arg.naifukuItems);
        mergeList(tonpukuItems, arg.tonpukuItems);
        mergeList(gaiyouItems, arg.gaiyouItems);
    }

    private void addShinryou(ShinryouFullDTO shinryou){
        HoukatsuKensaKind kind = HoukatsuKensaKind.fromCode(shinryou.master.houkatsukensa);
        if( kind == HoukatsuKensaKind.NONE ){
            mergeMapOne(simpleShinryouMap, shinryou.master.shinryoucode, new SimpleShinryouItem(shinryou.master));
        } else {
            extendList(houkatsuKensaItems, new HoukatsuKensaItem(shinryou.master));
        }
    }

    private void addDrug(DrugFullDTO drug){
        DrugCategory category = DrugCategory.fromCode(drug.drug.category);
        if( category != null ){
            switch(category){
                case Naifuku: {
                    extendList(naifukuItems, new NaifukuItem(drug));
                    break;
                }
                case Tonpuku: {
                    tonpukuItems.add(new TonpukuItem(drug));
                    break;
                }
                case Gaiyou: {
                    gaiyouItems.add(new GaiyouItem(drug));
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "RcptUnit{" +
                "simpleShinryouMap=" + simpleShinryouMap +
                ", houkatsuKensaItems=" + houkatsuKensaItems +
                ", naifukuItems=" + naifukuItems +
                ", tonpukuItems=" + tonpukuItems +
                ", gaiyouItems=" + gaiyouItems +
                '}';
    }

}

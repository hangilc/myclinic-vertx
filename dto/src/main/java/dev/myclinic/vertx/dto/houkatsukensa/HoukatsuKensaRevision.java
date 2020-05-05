package dev.myclinic.vertx.dto.houkatsukensa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HoukatsuKensaRevision {

    public String validFrom;
    public Map<String, List<HoukatsuKensaStep>> stepMap;

    public Optional<Integer> calcTen(String kind, int count){
        List<HoukatsuKensaStep> steps = stepMap.getOrDefault(kind, null);
        if( steps == null ){
            return Optional.empty();
        } else {
            for(HoukatsuKensaStep step: steps){
                if( step.threshold <= count ){
                    return Optional.of(step.point);
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "HoukatsuKensaRevision{" +
                "validFrom='" + validFrom + '\'' +
                ", stepMap=" + stepMap +
                '}';
    }

}

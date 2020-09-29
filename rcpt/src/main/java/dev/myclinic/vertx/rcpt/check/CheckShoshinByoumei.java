package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.dto.DiseaseFullDTO;

import java.util.List;
import java.util.stream.Collectors;

class CheckShoshinByoumei extends CheckBase {

    //private static Logger logger = LoggerFactory.getLogger(CheckShoshinByoumei.class);

    CheckShoshinByoumei(Scope scope) {
        super(scope);
    }

    void check(){
        forEachVisit(visit -> {
            int nShoshin = countShoshinGroup(visit);
            int nSaishin = countSaishinGroup(visit);
            if( nShoshin > 0 && nSaishin == 0 ){
                List<DiseaseFullDTO> ds = listDisease(visit);
                List<DiseaseFullDTO> starters = ds.stream()
                        .filter(d -> diseaseStartsAt(d, visit)).collect(Collectors.toList());
                if( starters.size() == 0 ){
                    error("初診時病名なし");
                }
                if( ds.size() > starters.size() ){
                    error("初診時に継続病名あり");
                }
            }
        });
    }

}

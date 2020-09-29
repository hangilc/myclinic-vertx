package dev.myclinic.vertx.rcpt;

import dev.myclinic.vertx.rcpt.resolvedmap.*;

import java.time.LocalDate;

public class Common {

    //private static Logger logger = LoggerFactory.getLogger(NewCommon2.class);

    private Common() {

    }

    public static ResolvedMap getMasterMaps(LocalDate at){
        ResolvedMap resolvedMap = new ResolvedMap();
        ResolvedShinryouMap resolvedShinryouMap = new ResolvedShinryouMap();
        ResolvedKizaiMap resolvedKizaiMap = new ResolvedKizaiMap();
        ResolvedDiseaseMap resolvedDiseaseMap = new ResolvedDiseaseMap();
        ResolvedDiseaseAdjMap resolvedDiseaseAdjMap = new ResolvedDiseaseAdjMap();
        return resolvedShinryouMap.resolveAt(at)
                .thenCompose(v -> {
                    resolvedMap.shinryouMap = resolvedShinryouMap;
                    return resolvedKizaiMap.resolveAt(at);
                })
                .thenCompose(v -> {
                    resolvedMap.kizaiMap = resolvedKizaiMap;
                    return resolvedDiseaseMap.resolveAt(at);
                })
                .thenCompose(v -> {
                    resolvedMap.diseaseMap = resolvedDiseaseMap;
                    return resolvedDiseaseAdjMap.resolveAt(at);
                })
                .thenApply(v -> {
                    resolvedMap.diseaseAdjMap = resolvedDiseaseAdjMap;
                    return resolvedMap;
                })
                .join();
    }
}

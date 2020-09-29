package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.dto.DiseaseNewDTO;
import dev.myclinic.vertx.dto.ShinryouDTO;

import java.util.List;

public interface Fixer {

    int enterShinryou(ShinryouDTO shinryou);
    boolean batchDeleteShinryou(List<Integer> shinryouIds);
    int enterDisease(DiseaseNewDTO disease);

}

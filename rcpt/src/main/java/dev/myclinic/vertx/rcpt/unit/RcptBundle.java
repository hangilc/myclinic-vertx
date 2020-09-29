package dev.myclinic.vertx.rcpt.unit;

import dev.myclinic.vertx.dto.VisitFull2DTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RcptBundle {

    private static Logger logger = LoggerFactory.getLogger(RcptBundle.class);
    private RcptUnit bundledUnit;

    public RcptBundle(List<VisitFull2DTO> visits) {
        bundledUnit = new RcptUnit();
        visits.forEach(visit -> {
            RcptUnit unit = new RcptUnit(visit);
            bundledUnit.merge(unit);
        });
    }

    public RcptUnit getBundledUnit() {
        return bundledUnit;
    }

    @Override
    public String toString() {
        return "RcptBundle{" +
                "bundledUnit=" + bundledUnit +
                '}';
    }
}

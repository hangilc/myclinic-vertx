package dev.myclinic.vertx.rcpt.data;

import dev.myclinic.vertx.dto.VisitDTO;

import java.util.Objects;

class HokenIds {

    int shahokokuhoId;
    int roujinId;
    int koukikoureiId;
    int kouhi1Id;
    int kouhi2Id;
    int kouhi3Id;

    HokenIds(VisitDTO visit) {
        this.shahokokuhoId = visit.shahokokuhoId;
        this.roujinId = visit.roujinId;
        this.koukikoureiId = visit.koukikoureiId;
        this.kouhi1Id = visit.kouhi1Id;
        this.kouhi2Id = visit.kouhi2Id;
        this.kouhi3Id = visit.kouhi3Id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HokenIds hokenIds = (HokenIds) o;
        return shahokokuhoId == hokenIds.shahokokuhoId &&
                roujinId == hokenIds.roujinId &&
                koukikoureiId == hokenIds.koukikoureiId &&
                kouhi1Id == hokenIds.kouhi1Id &&
                kouhi2Id == hokenIds.kouhi2Id &&
                kouhi3Id == hokenIds.kouhi3Id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(shahokokuhoId, roujinId, koukikoureiId, kouhi1Id, kouhi2Id, kouhi3Id);
    }
}

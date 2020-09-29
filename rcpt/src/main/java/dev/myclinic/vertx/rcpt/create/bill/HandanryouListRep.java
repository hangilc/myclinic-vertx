package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.Shinryou;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class HandanryouListRep {

    //private static Logger logger = LoggerFactory.getLogger(HandanryouListRep.class);
    private List<Integer> shinryoucodes;

    HandanryouListRep(List<Shinryou> handanryouList) {
        this.shinryoucodes = handanryouList.stream().map(shinryou -> shinryou.shinryoucode).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandanryouListRep that = (HandanryouListRep) o;
        return Objects.equals(shinryoucodes, that.shinryoucodes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(shinryoucodes);
    }
}

package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.input.Conduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class ConductRep {

    //private static Logger logger = LoggerFactory.getLogger(ConductRep.class);
    private List<ConductShinryouRep> shinryouRepList = new ArrayList<>();
    private List<ConductDrugRep> drugRepList = new ArrayList<>();
    private List<ConductKizaiRep> kizaiRepList = new ArrayList<>();

    ConductRep(Conduct conduct) {
        this.shinryouRepList = conduct.shinryouList.stream().map(ConductShinryouRep::new).collect(Collectors.toList());
        this.drugRepList = conduct.drugs.stream().map(ConductDrugRep::new).collect(Collectors.toList());
        this.kizaiRepList = conduct.kizaiList.stream().map(ConductKizaiRep::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConductRep that = (ConductRep) o;
        return Objects.equals(shinryouRepList, that.shinryouRepList) &&
                Objects.equals(drugRepList, that.drugRepList) &&
                Objects.equals(kizaiRepList, that.kizaiRepList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(shinryouRepList, drugRepList, kizaiRepList);
    }
}

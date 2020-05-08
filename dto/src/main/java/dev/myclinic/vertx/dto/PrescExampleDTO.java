package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

public class PrescExampleDTO {
    @Primary
    @AutoInc
    public int prescExampleId;
    public int iyakuhincode;
    public String masterValidFrom;
    public String amount;
    public String usage;
    public int days;
    public int category;
    public String comment;

    public PrescExampleDTO copy(){
        PrescExampleDTO dst = new PrescExampleDTO();
        dst.prescExampleId = prescExampleId;
        dst.iyakuhincode = iyakuhincode;
        dst.masterValidFrom = masterValidFrom;
        dst.amount = amount;
        dst.usage = usage;
        dst.days = days;
        dst.category = category;
        dst.comment = comment;
        return dst;
    }
}

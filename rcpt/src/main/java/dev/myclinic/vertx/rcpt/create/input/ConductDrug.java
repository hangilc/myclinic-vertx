package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConductDrug {

    @JsonProperty("医薬品コード")
    public int iyakuhincode;
    @JsonProperty("名称")
    public String name;
    @JsonProperty("用量")
    public double amount;
    @JsonProperty("単位")
    public String unit;
    @JsonProperty("薬価")
    public double yakka;

}

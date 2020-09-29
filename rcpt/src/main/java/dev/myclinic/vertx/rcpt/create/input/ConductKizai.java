package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConductKizai {

    @JsonProperty("器材コード")
    public int kizaicode;
    @JsonProperty("名称")
    public String name;
    @JsonProperty("量")
    public double amount;
    @JsonProperty("単位")
    public String unit;
    @JsonProperty("金額")
    public double kingaku;

}

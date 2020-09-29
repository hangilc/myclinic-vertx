package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Naifuku {

    @JsonProperty("医薬品コード")
    public int iyakuhincode;
    @JsonProperty("名称")
    public String name;
    @JsonProperty("用量")
    public double amount;
    @JsonProperty("単位")
    public String unit;
    @JsonProperty("用法")
    public String usage;
    @JsonProperty("薬価")
    public double yakka;
    @JsonProperty("麻毒")
    public char madoku;
    @JsonProperty("日数")
    public int days;
    @JsonProperty("摘要")
    public String tekiyou;

    @Override
    public String toString() {
        return "Naifuku{" +
                "iyakuhincode=" + iyakuhincode +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", unit='" + unit + '\'' +
                ", usage='" + usage + '\'' +
                ", yakka=" + yakka +
                ", madoku=" + madoku +
                ", days=" + days +
                ", tekiyou='" + tekiyou + '\'' +
                '}';
    }
}

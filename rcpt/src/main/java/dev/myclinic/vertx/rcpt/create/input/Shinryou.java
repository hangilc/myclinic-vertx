package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Shinryou {

    private static Logger logger = LoggerFactory.getLogger(Shinryou.class);

    @JsonProperty("診療コード")
    public int shinryoucode;
    @JsonProperty("名称")
    public String name;
    @JsonProperty("点数")
    public int tensuu;
    @JsonProperty("集計先")
    public String shuukeisaki;
    @JsonProperty("包括検査")
    public String houkatsuKensa;
    @JsonProperty("検査グループ")
    public String kensaGroup;
    @JsonProperty("摘要")
    public String tekiyou;

    public int getShinryoucode() {
        return shinryoucode;
    }

    public String getName() {
        return name;
    }

    public int getTensuu() {
        return tensuu;
    }

    public String getShuukeisaki() {
        return shuukeisaki;
    }

    public String getHoukatsuKensa() {
        return houkatsuKensa;
    }

    public String getKensaGroup() {
        return kensaGroup;
    }

    public String getTekiyou() {
        return tekiyou;
    }

    @Override
    public String toString() {
        return "Shinryou{" +
                "shinryoucode=" + shinryoucode +
                ", name='" + name + '\'' +
                ", tensuu=" + tensuu +
                ", shuukeisaki='" + shuukeisaki + '\'' +
                ", houkatsuKensa='" + houkatsuKensa + '\'' +
                ", kensaGroup='" + kensaGroup + '\'' +
                ", tekiyou='" + tekiyou + '\'' +
                '}';
    }
}

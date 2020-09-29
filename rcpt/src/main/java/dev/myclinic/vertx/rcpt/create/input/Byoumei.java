package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Byoumei {

    private static Logger logger = LoggerFactory.getLogger(Byoumei.class);

    @JsonProperty("名称")
    public String name;
    @JsonProperty("診療開始日")
    public String startDate;
    @JsonProperty("転帰")
    public String tenki;
    @JsonProperty("診療終了日")
    public String endDate;

}

package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conduct {

    @JsonProperty("ラベル")
    public String label;
    @JsonProperty("種類")
    public String kind;
    @JacksonXmlElementWrapper(localName="診療", useWrapping=false)
    @JsonProperty("診療")
    public List<ConductShinryou> shinryouList = new ArrayList<>();
    @JacksonXmlElementWrapper(localName="薬剤", useWrapping=false)
    @JsonProperty("薬剤")
    public List<ConductDrug> drugs = new ArrayList<>();
    @JacksonXmlElementWrapper(localName="器材", useWrapping=false)
    @JsonProperty("器材")
    public List<ConductKizai> kizaiList = new ArrayList<>();

}

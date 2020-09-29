package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Visit {

    //private static Logger logger = LoggerFactory.getLogger(Visit.class);

    @JsonProperty("受診日")
    public String visitedAt;
    @JacksonXmlElementWrapper(localName="診療", useWrapping=false)
    @JsonProperty("診療")
    public List<Shinryou> shinryouList = new ArrayList<>();
    @JsonProperty("投薬")
    public Drug drug = new Drug();
    @JacksonXmlElementWrapper(localName="行為", useWrapping=false)
    @JsonProperty("行為")
    public List<Conduct> conducts = new ArrayList<>();

    Visit() {

    }

    @Override
    public String toString() {
        return "Visit{" +
                "visitedAt='" + visitedAt + '\'' +
                ", shinryouList=" + shinryouList +
                ", drug=" + drug +
                '}';
    }
}

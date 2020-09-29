package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import dev.myclinic.vertx.util.kanjidate.Gengou;
import dev.myclinic.vertx.util.kanjidate.KanjiDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "レセプト")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rcpt {

    private static Logger logger = LoggerFactory.getLogger(Rcpt.class);

    @JsonProperty("元号")
    public String gengou;
    @JsonProperty("年")
    public int nen;
    @JsonProperty("月")
    public int month;
    @JsonProperty("都道府県番号")
    public int todoufukenBangou;
    @JsonProperty("医療機関コード")
    public String kikancode;
    @JsonProperty("医療機関住所")
    public String clinicAddress;
    @JsonProperty("医療機関電話")
    public String clinicPhone;
    @JsonProperty("医療機関名称")
    public String clinicName;
    @JacksonXmlElementWrapper(localName = "請求", useWrapping = false)
    @JsonProperty("請求")
    public List<Seikyuu> seikyuuList = new ArrayList<>();

    public LocalDate getDate(int day){
        Gengou geng = Gengou.fromKanjiRep(gengou);
        if( geng == null ){
            throw new RuntimeException("Unknown gengou: " + gengou);
        }
        int year = KanjiDate.gengouToYear(geng, nen);
        return LocalDate.of(year, month, day);
        //return DateTimeUtil.warekiToLocalDate(geng.getEra(), nen, month, day);
    }

}

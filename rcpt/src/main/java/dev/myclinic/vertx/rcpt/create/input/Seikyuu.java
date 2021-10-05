package dev.myclinic.vertx.rcpt.create.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Seikyuu {

    //private static Logger logger = LoggerFactory.getLogger(Seikyuu.class);

    @JsonProperty("患者番号")
    public int patientId;
    @JsonProperty("保険種別")
    public String hokenShubetsu;
    @JsonProperty("保険単独")
    public String hokenTandoku;
    @JsonProperty("保険負担")
    public String hokenFutan;
    @JsonProperty("給付割合")
    public int kyuufuWariai;
    @JsonProperty("保険者番号")
    public int hokenshaBangou;
    @JsonProperty("被保険者記号")
    public String hihokenshaKigou;
    @JsonProperty("被保険者番号")
    public  String hihokenshaBangou;
    @JsonProperty("被保険者枝番")
    public  String hihokenshaEdaban;
    @JsonProperty("氏名")
    public String shimei;
    @JsonProperty("性別")
    public String seibetsu;
    @JsonProperty("生年月日")
    public String birthday;
    @JsonProperty("公費1負担者番号")
    public int kouhiFutanshaBangou1;
    @JsonProperty("公費1受給者番号")
    public int kouhiJukyuushaBangou1;
    @JsonProperty("公費2負担者番号")
    public int kouhiFutanshaBangou2;
    @JsonProperty("公費2受給者番号")
    public int kouhiJukyuushaBangou2;
    @JacksonXmlElementWrapper(localName="傷病名", useWrapping=false)
    @JsonProperty("傷病名")
    public List<Byoumei> byoumeiList = new ArrayList<>();
    @JsonProperty("症状詳記")
    public String shouki;
    @JacksonXmlElementWrapper(localName="受診", useWrapping=false)
    @JsonProperty("受診")
    public List<Visit> visits = new ArrayList<>();
    @JsonProperty("特記事項")
    public String tokkiJikou = "";

    // used to sort seikyuu
    public String getRankTag() {
        return isKokuho() ? getKokuhoRank() : getShahoRank();
    }

    private boolean isKokuho() {
        int n = hokenshaBangou;
        if (n >= 10000 && n <= 999999) return true;
        if (n >= 67000000 && n <= 67999999) return true;
        return n >= 39000000 && n <= 39999999;
    }

    private String getKokuhoRank() {
        int n = hokenshaBangou;
        int fuken = (n % 1000000) / 10000;
        int tagKengai = (fuken == 13) ? 0 : 1;
        int tagKouhi = kouhiFutanshaBangou1 > 0 ? 1 : 2;
        int tagRank = 1;   // 一般
        if (n > 67000000)
            tagRank = 2;  // 退職
//        else if( $patient->find_text("老人受給者番号") !== null )
//        tagRank = 3;  // 老人
        else if (n >= 39000000 && n <= 39999999) // 後期高齢
            tagRank = 4;
        int tagSubrank = 0;
        switch (hokenFutan) {
            case "高齢９":
                tagSubrank = 2;
                break; // 退職高９
            case "高齢８":
                tagSubrank = 3;
                break;
            case "高齢７":
                tagSubrank = 4;
                break;
            case "本人":
                tagSubrank = 5;
                break;
            default:
                tagSubrank = 6;
                break;
        }
        String tagBangou = String.format("%06d", n % 1000000);
        return "T" + tagKengai + tagRank + tagSubrank + tagKouhi + tagBangou;
    }

    private String getShahoRank() {
        int tagRoujin = 0;
//        if( $patient->find_text("老人受給者番号") !== null )
//            tagRoujin = 1;
        int tagSubrank = 0;
        switch (hokenFutan) {
            case "高齢９":
                tagSubrank = 2;
                break;
            case "高齢８":
                tagSubrank = 3;
                break;
            case "高齢７":
                tagSubrank = 4;
                break;
            case "本人":
                tagSubrank = 5;
                break;
            case "家族":
                tagSubrank = 6;
                break;
            case "三才未満":
                tagSubrank = 7;
                break;
        }
        int n = hokenshaBangou;
        int tagHokengroup = 0;
        if (n >= 31000000 && n < 35000000)
            tagHokengroup = 1;
        else if (n >= 6000000 && n < 7000000)
            tagHokengroup = 2;
        else if (n >= 63000000)
            tagHokengroup = 3;
        String tagBangou = String.format("%06d", n % 1000000);
        return "U" + tagRoujin + tagSubrank + tagHokengroup + tagBangou;
    }

}

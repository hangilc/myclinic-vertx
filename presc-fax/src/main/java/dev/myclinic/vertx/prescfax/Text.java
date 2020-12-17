package dev.myclinic.vertx.prescfax;

import dev.myclinic.vertx.dto.HokenDTO;
import dev.myclinic.vertx.dto.KouhiDTO;
import dev.myclinic.vertx.util.HokenUtil;
import dev.myclinic.vertx.util.KouhiUtil;
import dev.myclinic.vertx.util.KoukikoureiUtil;
import dev.myclinic.vertx.util.ShahokokuhoUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Text {

    public String clinicAddress;
    public String clinicName;
    public String clinicPhone;
    public String kikancode;
    public String doctorName;
    public String hokenshaBangou;
    public String hihokensha;
    public String futansha;
    public String jukyuusha;
    public String futansha2;
    public String jukyuusha2;
    public String shimei;
    public String birthday;
    public String sex;
    public boolean honnin;
    public int futanWari;
    public String koufuDate;
    public String content;
    public String pharmacyName;

    public static List<Text> createFromData(Data data){
        LocalDate fromDate = LocalDate.parse(data.dateFrom);
        int year = fromDate.getYear();
        int month = fromDate.getMonthValue();
        List<Text> result = new ArrayList<>();
        for(ShohousenGroup g: data.groups){
            for(Presc presc: g.items){
                Text t = new Text();
                t.clinicAddress = data.clinicInfo.address;
                t.clinicName = data.clinicInfo.name;
                t.clinicPhone = data.clinicInfo.phone;
                t.kikancode = data.clinicInfo.kikancode;
                t.doctorName = data.clinicInfo.doctorName;
                t.hokenshaBangou = getHokenshaBangou(presc.hoken);
                t.hihokensha = getHihokensha(presc.hoken);
                t.futansha = getKouhiFutansha(presc.hoken.kouhi1);
                t.jukyuusha = getKouhiJukyuusha(presc.hoken.kouhi1);
                t.futansha2 = getKouhiFutansha(presc.hoken.kouhi2);
                t.jukyuusha2 = getKouhiJukyuusha(presc.hoken.kouhi2);
                t.shimei = presc.patient.lastName + presc.patient.firstName;
                t.birthday = presc.patient.birthday;
                t.sex = presc.patient.sex;
                t.honnin = isHonnin(presc.hoken);
                t.futanWari = HokenUtil.calcFutanWari(presc.hoken,
                        calcRcptAge(presc.patient.birthday, year, month),
                        presc.visit);
                t.koufuDate = presc.visit.visitedAt.substring(0, 10);
                t.content = presc.prescContent;
                t.pharmacyName = g.pharmacy.name;
                result.add(t);
            }
        }
        return result;
    }

    public static String getHokenshaBangou(HokenDTO hoken){
        if( hoken.shahokokuho != null ){
            return ShahokokuhoUtil.hokenshaBangouRep(hoken.shahokokuho.hokenshaBangou);
        } else if( hoken.koukikourei != null ){
            return hoken.koukikourei.hokenshaBangou;
        } else {
            System.err.printf("Unknown hokensha: %s\n", hoken.toString());
            return "";
        }
    }

    public static String getHihokensha(HokenDTO hoken){
        if( hoken.shahokokuho != null ){
            return ShahokokuhoUtil.hihokenshaRep(hoken.shahokokuho);
        } else if( hoken.koukikourei != null ){
            return hoken.koukikourei.hihokenshaBangou;
        } else {
            System.err.printf("Unknown hihokensha: %s\n", hoken.toString());
            return "";
        }
    }

    public static String getKouhiFutansha(KouhiDTO kouhi){
        if( kouhi == null ){
            return "";
        } else {
            return KouhiUtil.futanshaBangouString(kouhi.futansha);
        }
    }

    public static String getKouhiJukyuusha(KouhiDTO kouhi){
        if( kouhi == null ){
            return "";
        } else {
            return KouhiUtil.jukyuushaBangouString(kouhi.jukyuusha);
        }
    }

    public static boolean isHonnin(HokenDTO hoken){
        if( hoken.shahokokuho != null ){
            return hoken.shahokokuho.honnin != 0;
        } else {
            return false;
        }
    }

    public static int calcRcptAge(String birthday, int year, int month){
        LocalDate bd = LocalDate.parse(birthday);
        return HokenUtil.calcRcptAge(bd.getYear(), bd.getMonthValue(), bd.getDayOfMonth(), year, month);
    }

}

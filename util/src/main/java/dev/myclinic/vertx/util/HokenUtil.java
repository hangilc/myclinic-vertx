package dev.myclinic.vertx.util;

import dev.myclinic.vertx.dto.HokenDTO;
import dev.myclinic.vertx.dto.KouhiDTO;

import java.util.ArrayList;
import java.util.List;

public class HokenUtil {

    public static String hokenRep(Integer shahokokuhoHokenshaBangou, Integer shahokokuhoKoureiFutanWari,
                                  Integer koukikoureiFutanWari,
                                  Integer roujinFutanWari,
                                  Integer kouhi1FutanshaBangou,
                                  Integer kouhi2FutanshaBangou,
                                  Integer kouhi3FutanshaBangou) {
        List<String> terms = new ArrayList<>();
        if (shahokokuhoHokenshaBangou != null && shahokokuhoKoureiFutanWari != null) {
            terms.add(ShahokokuhoUtil.rep(shahokokuhoHokenshaBangou, shahokokuhoKoureiFutanWari));
        }
        if (koukikoureiFutanWari != null) {
            terms.add(KoukikoureiUtil.rep(koukikoureiFutanWari));
        }
        if (roujinFutanWari != null) {
            terms.add(RoujinUtil.rep(roujinFutanWari));
        }
        if (kouhi1FutanshaBangou != null) {
            terms.add(KouhiUtil.rep(kouhi1FutanshaBangou));
        }
        if (kouhi2FutanshaBangou != null) {
            terms.add(KouhiUtil.rep(kouhi2FutanshaBangou));
        }
        if (kouhi3FutanshaBangou != null) {
            terms.add(KouhiUtil.rep(kouhi3FutanshaBangou));
        }
        if( terms.size() == 0 ){
            return "［保険なし］";
        } else {
            return String.join("・", terms);
        }
    }

    public static void fillHokenRep(HokenDTO hoken) {
        Integer shahokokuhoHokenshaBangou = null;
        Integer shahokokuhoKoureiFutanWari = null;
        Integer koukikoureiFutanWari = null;
        Integer roujinFutanWari = null;
        Integer kouhi1FutanshaBangou = null;
        Integer kouhi2FutanshaBangou = null;
        Integer kouhi3FutanshaBangou = null;
        if (hoken.shahokokuho != null) {
            shahokokuhoHokenshaBangou = hoken.shahokokuho.hokenshaBangou;
            shahokokuhoKoureiFutanWari = hoken.shahokokuho.kourei;
        }
        if (hoken.koukikourei != null) {
            koukikoureiFutanWari = hoken.koukikourei.futanWari;
        }
        if (hoken.roujin != null) {
            roujinFutanWari = hoken.roujin.futanWari;
        }
        if (hoken.kouhi1 != null) {
            kouhi1FutanshaBangou = hoken.kouhi1.futansha;
        }
        if (hoken.kouhi2 != null) {
            kouhi2FutanshaBangou = hoken.kouhi2.futansha;
        }
        if (hoken.kouhi3 != null) {
            kouhi3FutanshaBangou = hoken.kouhi3.futansha;
        }
        hoken.rep = hokenRep(shahokokuhoHokenshaBangou, shahokokuhoKoureiFutanWari, koukikoureiFutanWari,
                roujinFutanWari, kouhi1FutanshaBangou, kouhi2FutanshaBangou, kouhi3FutanshaBangou);
    }

    public static int calcFutanWari(HokenDTO hoken, int rcptAge) {
        int futanWari = 10;
        if (hoken.shahokokuho != null) {
            futanWari = calcShahokokuhoFutanWariByAge(rcptAge);
            if (hoken.shahokokuho.kourei > 0) {
                futanWari = hoken.shahokokuho.kourei;
            }
        }
        if (hoken.koukikourei != null) {
            futanWari = hoken.koukikourei.futanWari;
        }
        if (hoken.roujin != null) {
            futanWari = hoken.roujin.futanWari;
        }
        for (KouhiDTO kouhi : new KouhiDTO[]{hoken.kouhi1, hoken.kouhi2, hoken.kouhi3}) {
            if (kouhi == null) {
                continue;
            }
            int kouhiFutanWari = kouhiFutanWari(kouhi.futansha);
            if (kouhiFutanWari < futanWari) {
                futanWari = kouhiFutanWari;
            }
        }
        return futanWari;
    }

    public static int calcRcptAge(int bdYear, int bdMonth, int bdDay, int atYear, int atMonth) {
        int age;
        age = atYear - bdYear;
        if (atMonth < bdMonth) {
            age -= 1;
        } else if (atMonth == bdMonth) {
            if (bdDay != 1) {
                age -= 1;
            }
        }
        return age;
    }

    public static int calcShahokokuhoFutanWariByAge(int age) {
        if (age < 3)
            return 2;
        else if (age >= 70)
            return 2;
        else
            return 3;
    }

    public static int kouhiFutanWari(int futanshaBangou) {
        if (futanshaBangou / 1000000 == 41)
            return 1;
        else if ((futanshaBangou / 1000) == 80136)
            return 1;
        else if ((futanshaBangou / 1000) == 80137)
            return 0;
        else if ((futanshaBangou / 1000) == 81136)
            return 1;
        else if ((futanshaBangou / 1000) == 81137)
            return 0;
        else if ((futanshaBangou / 1000000) == 88)
            return 0;
        else {
            System.out.println("unknown kouhi futansha: " + futanshaBangou);
            return 0;
        }
    }

    public static String formatShahokokuhoHokenshaBangou(int bangou) {
        if (bangou <= 9999) {
            return String.format("%d", bangou);
        } else if (bangou <= 999999) {
            return String.format("%06d", bangou);
        } else {
            return String.format("%08d", bangou);
        }
    }

    public static String formatKouhiJukyuushaBangou(int bangou){
        return String.format("%07d", bangou);
    }

}

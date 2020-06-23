package dev.myclinic.vertx.shohousendrawer;

import dev.myclinic.vertx.drawer.DrawerColor;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.util.HokenUtil;
import dev.myclinic.vertx.util.ShahokokuhoUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShohousenData {

    public String clinicAddress = "";
    public String clinicName = "";
    public String clinicPhone = "";
    public String kikancode = "";
    public String doctorName = "";
    public String hokenshaBangou = "";
    public String hihokensha = "";
    public String futansha = "";
    public String jukyuusha = "";
    public String futansha2 = "";
    public String jukyuusha2 = "";
    public String shimei = "";
    public LocalDate birthday;
    public String sex;
    public Boolean honnin = null;
    public Integer futanWari = null;
    public LocalDate koufuDate;
    public LocalDate validUptoDate;
    public List<String> drugLines;
    public String pharmacyName;
    public String memo = "";

    public void applyTo(ShohousenDrawer drawer){
        drawer.setHakkouKikan(clinicAddress, clinicName, clinicPhone, kikancode);
        drawer.setDoctorName(doctorName);
        drawer.setHokenshaBangou(hokenshaBangou);
        drawer.setHihokensha(hihokensha);
        drawer.setKouhi1Futansha(futansha);
        drawer.setKouhi1Jukyuusha(jukyuusha);
        drawer.setKouhi2Futansha(futansha2);
        drawer.setKouhi2Jukyuusha(jukyuusha2);
        drawer.setShimei(shimei);
        if( birthday != null ){
            drawer.setBirthday(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());
        }
        if( sex != null ) {
            switch (sex) {
                case "M": case "男":
                    drawer.setSexMale();
                    break;
                case "F": case "女":
                    drawer.setSexFemale();
                    break;
            }
        }
        if( honnin != null ){
            if( honnin ){
                drawer.setKubunHihokensha();
            } else {
                drawer.setKubunHifuyousha();
            }
        }
        if( futanWari != null ){
            if( futanWari != 10 ){
                drawer.setFutanWari(futanWari);
            }
        }
        if( koufuDate != null ){
            drawer.setKoufuDate(koufuDate.getYear(), koufuDate.getMonthValue(), koufuDate.getDayOfMonth());
        }
        if( validUptoDate != null ){
            if (koufuDate != null && validUptoDate.equals(koufuDate.plusDays(3))) {
                // it is the default value, so omit printing
            } else {
                drawer.setValidUptoDate(validUptoDate.getYear(), validUptoDate.getMonthValue(), validUptoDate.getDayOfMonth());
            }
        }
        if( pharmacyName != null && !pharmacyName.isEmpty() ){
            drawer.setPharmacyName(pharmacyName);
        }
        if( drugLines == null ){
            drugLines = Collections.emptyList();
        }
        if( memo == null ){
            memo = "";
        }
        drawer.setDrugLinesAndMemo(drugLines, memo);
//        if( drugLines != null ){
//            drawer.setDrugLines(drugLines);
//        }
//        if( memo != null && !memo.isEmpty() ){
//            drawer.setMemo(memo);
//        }
    }

    public void setClinicInfo(ClinicInfoDTO clinicInfo){
        if( clinicInfo != null ){
            clinicAddress = clinicInfo.postalCode + " " + clinicInfo.address;
            clinicName = clinicInfo.name;
            clinicPhone = "電話 " + clinicInfo.tel;
            kikancode = clinicInfo.todoufukencode + clinicInfo.tensuuhyoucode + clinicInfo.kikancode;
            doctorName = clinicInfo.doctorName;
        }
    }

    public void setHoken(HokenDTO hoken){
        if( hoken != null ){
            if( hoken.shahokokuho != null ){
                hokenshaBangou = ShahokokuhoUtil.hokenshaBangouRep(hoken.shahokokuho.hokenshaBangou);
                hihokensha = composeHihokensha(hoken.shahokokuho);
                this.honnin = hoken.shahokokuho.honnin != 0;
            } else if( hoken.koukikourei != null ){
                hokenshaBangou = hoken.koukikourei.hokenshaBangou;
                hihokensha = hoken.koukikourei.hihokenshaBangou;
            }
            List<KouhiDTO> kouhiList = new ArrayList<>();
            if( hoken.kouhi1 != null ){
                kouhiList.add(hoken.kouhi1);
                if( hoken.kouhi2 != null ){
                    kouhiList.add(hoken.kouhi2);
                    if( hoken.kouhi3 != null ){
                        kouhiList.add(hoken.kouhi3);
                    }
                }
            }
            if( kouhiList.size() > 0 ){
                KouhiDTO kouhi = kouhiList.get(0);
                futansha = kouhi.futansha + "";
                jukyuusha = kouhi.jukyuusha + "";
                if( kouhiList.size() > 1 ){
                    KouhiDTO kouhi2 = kouhiList.get(1);
                    futansha2 = kouhi2.futansha + "";
                    jukyuusha2 = kouhi2.jukyuusha + "";
                }
            }
        }
    }

    public void setPatient(PatientDTO patient){
        shimei = patient.lastName + patient.firstName;
        if( patient.birthday != null && !"0000-00-00".equals(patient.birthday) ){
            this.birthday = LocalDate.parse(patient.birthday);
        }
        sex = patient.sex;
    }

    public void setFutanWari(int futanWari){
        this.futanWari = futanWari;
    }

    public void setKoufuDate(LocalDate koufuDate){
        this.koufuDate = koufuDate;
    }

    public void setValidUptoDate(LocalDate date){
        this.validUptoDate = date;
    }

//    private final Pattern patValidUptoDate = Pattern.compile("@有効期限\\s*[:：]\\s*(\\d{4}-\\d{2}-\\d{2})\\s*");
//    private final Pattern pat0410 = Pattern.compile("@(0410|０４１０)対応"); //新型コロナ感染対策
    private final Pattern patValidUptoDate = Pattern.compile("@有効期限\\s*[:：]\\s*(\\d{4}-\\d{2}-\\d{2})\\s*");
    private final Pattern pat0410 = Pattern.compile("@(0410|０４１０)対応([+＋]?)"); //新型コロナ感染対策
    private final Pattern patMemo = Pattern.compile("@memo[:：]\\s*(.+)");

    public void setDrugs(String content){
        if( content != null ){
            content = content.trim();
            List<String> lines = Collections.emptyList();
            if( !content.isEmpty() && !content.startsWith("@") ) {
                lines = Arrays.stream(content.split("\\s*(?:\\r\\n|\\r|\\n)"))
                        .collect(Collectors.toList());
            }
            if( lines.size() > 0 && lines.get(0).startsWith("院外処方") ){
                lines.remove(0);
            }
            List<String> dLines = new ArrayList<>();
            for(String line: lines){
                if( line.startsWith("@") ){
                    Matcher m = patValidUptoDate.matcher(line);
                    if( m.matches() ){
                        String value = m.group(1);
                        LocalDate d = LocalDate.parse(value);
                        setValidUptoDate(d);
                        continue;
                    }
                    m = pat0410.matcher(line);
                    if( m.matches() ){
                        if( m.group(2) != null && !m.group(2).equals("") ){
                            //noinspection StringConcatenationInLoop
                            memo = memo + "0410対応（本人来店・原本郵送）\n";
                        } else {
                            //noinspection StringConcatenationInLoop
                            memo = memo + "0410対応\n";
                        }
                        continue;
//                        //noinspection StringConcatenationInLoop
//                        memo = memo + "0410対応\n";
//                        continue;
                    }
                    m = patMemo.matcher(line);
                    if( m.matches() ){
                        //noinspection StringConcatenationInLoop
                        memo = memo + m.group(1) + "\n";
                        continue;
                    }
                    throw new RuntimeException("Unknown command: " + line + "\n" +
                            "@有効期限：2020-04-19\n" +
                            "@memo:...\n" +
                            "@0410対応(+|＋)");
                } else {
                    dLines.add(line);
                }
            }
            if( dLines.size() > 0 ) {
                dLines.add("------以下余白------");
            }
            this.drugLines = dLines;
        }
    }

    private String composeHihokensha(ShahokokuhoDTO shahokokuho){
        String kigou = shahokokuho.hihokenshaKigou;
        String bangou = shahokokuho.hihokenshaBangou;
        if( kigou == null || kigou.isEmpty() ){
            if( bangou == null || bangou.isEmpty() ){
                return "";
            } else {
                return bangou;
            }
        } else {
            if( bangou == null || bangou.isEmpty() ){
                return kigou;
            } else {
                return kigou + " ・ " + bangou;
            }
        }
    }
}

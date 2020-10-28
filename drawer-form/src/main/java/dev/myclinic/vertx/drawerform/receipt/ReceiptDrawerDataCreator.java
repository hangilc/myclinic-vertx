package dev.myclinic.vertx.drawerform.receipt;

import dev.myclinic.vertx.consts.MeisaiSection;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.util.DateTimeUtil;
import dev.myclinic.vertx.util.HokenUtil;

import java.text.NumberFormat;
import java.time.LocalDate;

public class ReceiptDrawerDataCreator {

    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private ReceiptDrawerData data;

    public static ReceiptDrawerData create(MeisaiDTO meisai, PatientDTO patient, VisitDTO visit,
        Integer charge, ClinicInfoDTO clinicInfo){
        ReceiptDrawerDataCreator creator = new ReceiptDrawerDataCreator();
        ReceiptDrawerData data =creator.data;
        if( patient != null ) {
            creator.setPatient(patient);
        }
        if( meisai != null ) {
            if( charge != null ) {
                creator.setCharge(charge);
            }
        }
        if( visit != null ) {
            creator.setVisitDate(visit);
        }
        creator.setIssueDate();
        if( meisai != null ) {
            creator.setMeisai(meisai);
        }
        if( clinicInfo != null ) {
            creator.setClinicInfo(clinicInfo);
        }
        return data;
    }

    public ReceiptDrawerDataCreator(){
        this.data = new ReceiptDrawerData();
    }

    public ReceiptDrawerData getData(){
        return data;
    }

    public void setPatient(PatientDTO patient){
        data.setPatientName(patient.lastName + patient.firstName);
        data.setPatientId("" + patient.patientId);
    }

    public void setCharge(int charge){
        data.setChargeByInt(charge);
    }

    public void setVisitDate(VisitDTO visit){
        data.setVisitDate(DateTimeUtil.sqlDateTimeToKanji(visit.visitedAt, DateTimeUtil.kanjiFormatter1, null));
    }

    public void setIssueDate(){
        data.setIssueDate(DateTimeUtil.toKanji(LocalDate.now(), DateTimeUtil.kanjiFormatter1));
    }

    public void setMeisai(MeisaiDTO meisai){
        setHoken(meisai.hoken);
        setFutanWari(meisai.futanWari);
        for(MeisaiSectionDTO section: meisai.sections){
            String ten = format(section.sectionTotalTen);
            switch(MeisaiSection.valueOf(section.name)){
                case ShoshinSaisin: data.setShoshin(ten); break;
                case IgakuKanri: data.setKanri(ten); break;
                case Zaitaku: data.setZaitaku(ten); break;
                case Kensa: data.setKensa(ten); break;
                case Gazou: data.setGazou(ten); break;
                case Touyaku: data.setTouyaku(ten); break;
                case Chuusha: data.setChuusha(ten); break;
                case Shochi: data.setShochi(ten); break;
                case Sonota: data.setSonota(ten); break;
                default: System.out.println("unknown meisai section: " + section.name);
            }
        }
        setSouten(meisai.totalTen);
    }

    public void setHoken(HokenDTO hoken){
        data.setHoken(hokenRep(hoken));
    }

    public void setFutanWari(int futanWari){
        if( futanWari == 10 ){
            data.setFutanWari("");
        } else {
            data.setFutanWari("" + futanWari);
        }
    }

    public void setSouten(int souten){
        data.setSouten(format(souten));
    }

    public void setClinicInfo(ClinicInfoDTO clinicInfo){
        data.setClinicName(clinicInfo.name);
        data.setAddressLines(new String[]{
                clinicInfo.postalCode,
                clinicInfo.address,
                clinicInfo.tel,
                clinicInfo.fax,
                clinicInfo.homepage
        });
    }

    private String hokenRep(HokenDTO hoken){
        if( hoken != null ){
            if( hoken.rep == null ){
                HokenUtil.fillHokenRep(hoken);
            }
            String rep = hoken.rep;
            if( !rep.isEmpty() ){
                return rep;
            }
        }
        return "自費";
    }

    private String format(int number){
        return numberFormat.format(number);
    }
}

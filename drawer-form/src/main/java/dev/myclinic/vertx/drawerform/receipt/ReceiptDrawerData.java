package dev.myclinic.vertx.drawerform.receipt;

import java.text.NumberFormat;

/**
 * Created by hangil on 2017/05/21.
 */
public class ReceiptDrawerData {

    private String patientName = "";
    private String charge = "";
    private String visitDate = "";
    private String issueDate = "";
    private String patientId = "";
    private String hoken = "";
    private String futanWari = "";
    private String shoshin = "";
    private String kanri = "";
    private String zaitaku = "";
    private String kensa = "";
    private String gazou = "";
    private String touyaku = "";
    private String chuusha = "";
    private String shochi = "";
    private String sonota = "";
    private String souten = "";
    private String[] hokengai = new String[]{ "", "", "", "" };
    private String clinicName = "";
    private String[] addressLines = new String[]{};
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public void setChargeByInt(int charge){
        this.charge = numberFormat.format(charge);
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getHoken() {
        return hoken;
    }

    public void setHoken(String hoken) {
        this.hoken = hoken;
    }

    public String getFutanWari() {
        return futanWari;
    }

    public void setFutanWari(String futanWari) {
        this.futanWari = futanWari;
    }

    public String getShoshin() {
        return shoshin;
    }

    public void setShoshin(String shoshin) {
        this.shoshin = shoshin;
    }

    public String getKanri() {
        return kanri;
    }

    public void setKanri(String kanri) {
        this.kanri = kanri;
    }

    public String getZaitaku() {
        return zaitaku;
    }

    public void setZaitaku(String zaitaku) {
        this.zaitaku = zaitaku;
    }

    public String getKensa() {
        return kensa;
    }

    public void setKensa(String kensa) {
        this.kensa = kensa;
    }

    public String getGazou() {
        return gazou;
    }

    public void setGazou(String gazou) {
        this.gazou = gazou;
    }

    public String getTouyaku() {
        return touyaku;
    }

    public void setTouyaku(String touyaku) {
        this.touyaku = touyaku;
    }

    public String getChuusha() {
        return chuusha;
    }

    public void setChuusha(String chuusha) {
        this.chuusha = chuusha;
    }

    public String getShochi() {
        return shochi;
    }

    public void setShochi(String shochi) {
        this.shochi = shochi;
    }

    public String getSonota() {
        return sonota;
    }

    public void setSonota(String sonota) {
        this.sonota = sonota;
    }

    public String getSouten() {
        return souten;
    }

    public void setSouten(String souten) {
        this.souten = souten;
    }

    public String[] getHokengai(){
        return hokengai;
    }

    public String getHokengai(int i){
        return hokengai[i];
    }

    public void setHokengai(int i, String text){
        hokengai[i] = text;
    }

    public void setHokengai(String[] hokengai) {
        this.hokengai = hokengai;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String[] getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(String[] addressLines) {
        this.addressLines = addressLines;
    }
}

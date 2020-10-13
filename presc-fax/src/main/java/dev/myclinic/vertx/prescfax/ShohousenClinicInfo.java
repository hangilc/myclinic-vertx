package dev.myclinic.vertx.prescfax;

import dev.myclinic.vertx.dto.ClinicInfoDTO;

public class ShohousenClinicInfo {

    public final String address;
    public final String name;
    public final String phone;
    public final String kikancode;
    public final String doctorName;

    public static ShohousenClinicInfo fromClinicInfoDTO(ClinicInfoDTO c){
        String address = c.postalCode + " " + c.address;
        String phone = "電話 " + c.tel;
        String kikancode = String.format("%s%s%s", c.todoufukencode,
                c.tensuuhyoucode, c.kikancode);
        return new ShohousenClinicInfo(address, c.name, phone, kikancode, c.doctorName);
    }

    public ShohousenClinicInfo(String address, String name, String phone, String kikancode,
                               String doctor_name) {
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.kikancode = kikancode;
        this.doctorName = doctor_name;
    }

    @Override
    public String toString() {
        return "ShohousenClinicInfo{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", kikancode='" + kikancode + '\'' +
                ", doctorName='" + doctorName + '\'' +
                '}';
    }
}

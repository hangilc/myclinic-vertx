package dev.myclinic.vertx.prescfax;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.myclinic.vertx.dto.ClinicInfoDTO;

public class ShohousenClinicInfo {

    public final String address;
    public final String name;
    public final String phone;
    public final String kikancode;
    @JsonProperty("doctor_name")
    public final String doctorName;

    public static ShohousenClinicInfo fromClinicInfoDTO(ClinicInfoDTO c){
        String address = c.postalCode + " " + c.address;
        String phone = "電話 " + c.tel;
        String kikancode = String.format("%s%s%s", c.todoufukencode,
                c.tensuuhyoucode, c.kikancode);
        return new ShohousenClinicInfo(address, c.name, phone, kikancode, c.doctorName);
    }

    public ShohousenClinicInfo(@JsonProperty("address") String address,
                               @JsonProperty("name") String name,
                               @JsonProperty("phone") String phone,
                               @JsonProperty("kikancode")String kikancode,
                               @JsonProperty("doctor_name") String doctorName) {
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.kikancode = kikancode;
        this.doctorName = doctorName;
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

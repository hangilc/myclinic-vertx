package dev.myclinic.vertx.prescfax;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.myclinic.vertx.client2.Client;
import dev.myclinic.vertx.dto.ClinicInfoDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data {

    @JsonProperty("date_from")
    public final String dateFrom;
    @JsonProperty("date_upto")
    public final String dateUpto;
    @JsonProperty("clinic_info")
    public final ShohousenClinicInfo clinicInfo;
    public final List<ShohousenGroup> groups;

    public Data(@JsonProperty("date_from") String dateFrom,
                @JsonProperty("date_upto") String dateUpto,
                @JsonProperty("clinic_info") ShohousenClinicInfo clinicInfo,
                @JsonProperty("groups") List<ShohousenGroup> groups) {
        this.dateFrom = dateFrom;
        this.dateUpto = dateUpto;
        this.clinicInfo = clinicInfo;
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "Data{" +
                "dateFrom='" + dateFrom + '\'' +
                ", dateUpto='" + dateUpto + '\'' +
                ", clinicInfo=" + clinicInfo +
                ", groups=" + groups +
                '}';
    }

    public static Data create(Client client, LocalDate from, LocalDate upto)
            throws IOException, InterruptedException {
        List<Presc> prescList = Presc.listPresc(client, from, upto);
        Map<String, List<Presc>> groupMap = new HashMap<>();
        for(Presc presc: prescList){
            String fax = presc.fax;
            List<Presc> group = groupMap.computeIfAbsent(fax, k -> new ArrayList<>());
            group.add(presc);
        }
        List<Pharmacy> pharmaList = Pharmacy.getList();
        Map<String, Pharmacy> pharmaMap = new HashMap<>();
        for(Pharmacy p: pharmaList){
            String fax = p.fax;
            pharmaMap.put(fax, p);
        }
        List<ShohousenGroup> sgList = new ArrayList<>();
        for(String fax: groupMap.keySet()){
            Pharmacy pharma = pharmaMap.get(fax);
            if( pharma == null ){
                throw new RuntimeException("Cannot get pharmacy of fax " + fax);
            }
            sgList.add(new ShohousenGroup(pharma, groupMap.get(fax)));
        }
        sgList.sort(Comparator.<ShohousenGroup>comparingInt(sg -> sg.items.size()).reversed());
        ClinicInfoDTO clinicInfo = client.getClinicInfo();
        return new Data(from.toString(), upto.toString(), ShohousenClinicInfo.fromClinicInfoDTO(clinicInfo), sgList);
    }

    private final static Pattern prescPattern = Pattern.compile("^\\s*院外処方\\s*\\r?\\n");

    public static boolean isPrescContent(String content) {
        Matcher m = prescPattern.matcher(content);
        return m.find();
    }

    private final static Pattern presc0410Pattern = Pattern.compile("\\n@0410対応");

    public static boolean is0410Presc(String content){
        Matcher m = presc0410Pattern.matcher(content);
        return m.find();
    }

    private final static Pattern pharmaFaxPattern = Pattern.compile("^(.+)にファックス（(\\+\\d+)）");

    public static class PharmaFax {
        public String pharma;
        public String fax;

        public PharmaFax(String pharma, String fax) {
            this.pharma = pharma;
            this.fax = fax;
        }
    }

    public static PharmaFax isPharmaFax(String content) {
        Matcher m = pharmaFaxPattern.matcher(content);
        if (m.find()) {
            return new PharmaFax(m.group(1), m.group(2));
        } else {
            return null;
        }
    }

    private final static Pattern prescNotFaxPattern = Pattern.compile(
            "処方箋を渡した|自宅に処方箋を郵送|(電子)?メールで処方箋を送付した|処方箋を自宅にファックスで送った");

    public static boolean isPrescNotFax(String content) {
        Matcher m = prescNotFaxPattern.matcher(content);
        return m.find();
    }

}

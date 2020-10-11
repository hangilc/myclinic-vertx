package dev.myclinic.vertx.prescfax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pharmacy {

    private static final Pattern namePattern = Pattern.compile("\\s*【(.+)】.*");
    private static final Pattern telPattern = Pattern.compile("tel:\\s*(.+)");
    private static final Pattern faxPattern = Pattern.compile("fax:\\s*(.+)");
    private static final Pattern addrPattern = Pattern.compile("〒(\\d{3}-\\d{4})\\s+(.+)");

    public final String name;
    public final String tel;
    public final String fax;
    public final String postalCode;
    public final String addr;

    public Pharmacy(String name, String tel, String fax, String postalCode, String addr) {
        this.name = name;
        this.tel = tel;
        this.fax = fax;
        this.postalCode = postalCode;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "Pharmacy{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", fax='" + fax + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", addr='" + addr + '\'' +
                '}';
    }

    private static class Stage {
        private String sName;
        private String sTel;
        private String sFax;
        private String sPostalCode;
        private String sAddr;

        public void flush(List<Pharmacy> list) {
            if (sName == null && sTel == null && sFax == null && sAddr == null) {
                return;
            }
            if (sName == null) {
                throw new RuntimeException("Missing pharmacy name.");
            }
            if (sTel == null) {
                System.err.printf("name: %s\n", sName);
                throw new RuntimeException("Missing pharmacy tel.");
            }
            if (sFax == null) {
                throw new RuntimeException("Missing pharmacy fax.");
            }
            if (sAddr == null) {
                throw new RuntimeException("Missing pharmacy address.");
            }
            list.add(new Pharmacy(sName, sTel, sFax, sPostalCode, sAddr));
            this.sName = null;
            this.sTel = null;
            this.sFax = null;
            this.sPostalCode = null;
            this.sAddr = null;
        }

        public void setName(String name) {
            if (this.sName != null) {
                throw new RuntimeException("Cannot set name.");
            }
            this.sName = name;
        }

        public void setTel(String tel) {
            if (this.sTel != null) {
                throw new RuntimeException("Cannot set tel.");
            }
            this.sTel = tel;
        }

        public void setFax(String fax) {
            if (this.sFax != null) {
                throw new RuntimeException("Cannot set fax.");
            }
            this.sFax = fax;
        }

        public void setAddr(String postalCode, String addr) {
            if (this.sPostalCode != null) {
                throw new RuntimeException("Cannot set postal code.");
            }
            if (this.sAddr != null) {
                throw new RuntimeException("Cannot set address.");
            }
            this.sPostalCode = postalCode;
            this.sAddr = addr;
        }
    }

    public static List<Pharmacy> readFromFile(String file) throws IOException {
        List<Pharmacy> result = new ArrayList<>();
        List<String> lines = Files.readAllLines(Path.of(file));
        Stage stage = new Stage();
        Matcher m;
        for (String line : lines) {
            m = namePattern.matcher(line);
            if (m.matches()) {
                String name = m.group(1);
                stage.flush(result);
                stage.setName(name);
                continue;
            }
            m = telPattern.matcher(line);
            if (m.matches()) {
                String tel = m.group(1);
                stage.setTel(tel);
                continue;
            }
            m = faxPattern.matcher(line);
            if (m.matches()) {
                String fax = m.group(1);
                stage.setFax(fax);
                continue;
            }
            m = addrPattern.matcher(line);
            if (m.matches()) {
                String pc = m.group(1);
                String addr = m.group(2);
                stage.setAddr(pc, addr);
                continue;
            }
            if( "".equals(line.trim()) ){
                continue;
            }
            System.err.printf("Unknown pharmacy line: %s\n", line);
        }
        stage.flush(result);
        return result;
    }

}

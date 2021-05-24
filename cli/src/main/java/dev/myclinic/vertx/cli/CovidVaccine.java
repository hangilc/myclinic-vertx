package dev.myclinic.vertx.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class CovidVaccine {

    private static String CovidVaccineDir = System.getenv("COVID_VACCINE_DIR");

    public static void main(String[] args) throws Exception {
        String stamp = Misc.makeTimestamp();
        System.out.println(stamp);
        List<RegularPatient> rps = readRegularPatients();
        List<RegularPatient> candidates = rps.stream()
                .filter(rp -> !rp.excluded && rp.age >= 65).collect(toList());
        candidates.forEach(System.out::println);
        System.out.println(candidates.size());
    }

    private static class RegularPatient {
        int patientId;
        String name;
        int age;
        String phone;
        boolean excluded;

        @Override
        public String toString() {
            return "RegularPatient{" +
                    "patientId=" + patientId +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", phone='" + phone + '\'' +
                    ", excluded=" + excluded +
                    '}';
        }
    }

    private static List<RegularPatient> readRegularPatients(){
        Path file = Path.of(CovidVaccineDir, "regular-patients.txt");
        List<String> lines = Misc.readLines(file.toString());
        return lines.stream().map(CovidVaccine::parseRegularPatient)
                .filter(Objects::nonNull).collect(toList());
    }

    private static RegularPatient parseRegularPatient(String line){
        if( line == null || line.isEmpty() ){
            return null;
        } else {
            RegularPatient rp = new RegularPatient();
            char ch = line.charAt(0);
            if( ch == 'x' ){
                rp.excluded = true;
                line = line.substring(1).stripLeading();
            }
            String[] parts = line.split("\\s+", 4);
            rp.patientId = Integer.parseInt(parts[0]);
            rp.name = parts[1];
            rp.age = Integer.parseInt(parts[2]);
            rp.phone = parts[3];
            return rp;
        }
    }

}

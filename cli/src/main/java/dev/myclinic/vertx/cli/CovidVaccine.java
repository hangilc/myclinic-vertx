package dev.myclinic.vertx.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class CovidVaccine {

    private final static String CovidVaccineDir = System.getenv("COVID_VACCINE_DIR");

    public static void main(String[] args) throws Exception {
        List<RegularPatient> rps = readRegularPatients();
        List<RegularPatient> patients = rps.stream()
                .filter(rp -> !rp.excluded && rp.age >= 65).collect(toList());
        List<RegularPatient> over65Candidates = listOver65Candidates(patients);
        saveOver65Candidates(over65Candidates);
        saveOver65CandidatePatientIds(over65Candidates);
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

    private static List<RegularPatient> listOver65Candidates(List<RegularPatient> patients){
        return patients.stream().filter(
                p -> !p.excluded && p.age >= 65
        ).collect(toList());
    }

    private static void saveOver65Candidates(List<RegularPatient> candidates){
        Path file = Path.of(CovidVaccineDir, "over65-candidates.txt");
        List<String> lines = candidates.stream()
                .map(p -> String.format("%d %s %s", p.patientId, p.name, p.phone))
                .collect(toList());
        Misc.saveLines(file.toString(), lines);
    }

    private static void saveOver65CandidatePatientIds(List<RegularPatient> candidates){
        Path file = Path.of(CovidVaccineDir, "over65-candidate-patient-ids.txt");
        String content = candidates.stream()
                .map(p -> p.patientId)
                .sorted()
                .map(patientId -> String.format("%d", patientId))
                .collect(joining(", "));
        Misc.saveString(file.toString(), content);
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

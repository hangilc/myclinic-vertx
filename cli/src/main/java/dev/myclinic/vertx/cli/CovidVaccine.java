package dev.myclinic.vertx.cli;

import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class CovidVaccine {

    private final static String CovidVaccineDir = System.getenv("COVID_VACCINE_DIR");

    public static void main(String[] args) throws Exception {
        List<RegularPatient> rps = readRegularPatients();
        List<RegularPatient> candidates = rps.stream()
                .filter(rp -> !rp.isExcluded()).collect(toList());
        List<RegularPatient> over65Candidates = candidates.stream()
                .filter(rp -> rp.age >= 65).collect(toList());
        saveOver65Candidates(over65Candidates);
        saveOver65CandidatePatientIds(over65Candidates);
    }

    private static class RegularPatient {
        int patientId;
        String name;
        int age;
        String phone;
        String attr;

        public boolean isExcluded(){
            return attr.startsWith("x") || attr.startsWith("X");
        }

        @Override
        public String toString() {
            return String.format("%s %d %s %d才 %s", attr, patientId, name, age, phone);
        }
    }

    private static RegularPatient parseRegularPatient(String line){
        if( line == null || line.isEmpty() ){
            return null;
        } else {
            RegularPatient rp = new RegularPatient();
            char ch = line.charAt(0);
            if( Character.isDigit(ch) ){
                line = "* " + line;
            }
            String[] parts = line.split("\\s+", 5);
            String attr = parts[0];
            rp.attr = attr.equals("*") ? "" : attr;
            rp.patientId = Integer.parseInt(parts[1]);
            rp.name = parts[2];
            rp.age = Integer.parseInt(parts[3]);
            rp.phone = parts[4];
            return rp;
        }
    }

    private static List<RegularPatient> readRegularPatients(){
        Path file = Path.of(CovidVaccineDir, "regular-patients.txt");
        List<String> lines = Misc.readLines(file.toString());
        return lines.stream().map(CovidVaccine::parseRegularPatient)
                .filter(Objects::nonNull).collect(toList());
    }

    private static void saveOver65Candidates(List<RegularPatient> candidates){
        Map<String,List<RegularPatient>> groups = new HashMap<>();
        candidates.forEach(p -> {
            String code = p.attr;
            if( code.length() > 0 ){
                code = code.substring(0, 1).toUpperCase();
            }
            List<RegularPatient> list = groups.computeIfAbsent(code, k -> new ArrayList<>());
            list.add(p);
        });
        List<String> codes = new ArrayList<>();
        Set<String> keys = new HashSet<>(groups.keySet());
        List.of("C", "S", "", "T").forEach(c -> {
            codes.add(c);
            keys.remove(c);
        });
        codes.addAll(keys);
        List<String> lines = new ArrayList<>();
        for(String code: codes){
            List<RegularPatient> list = groups.getOrDefault(code, Collections.emptyList());
            list.forEach(p -> lines.add(p.toString()));
            lines.add("");
        }
        Path file = Path.of(CovidVaccineDir, "over65-candidates.txt");
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

}

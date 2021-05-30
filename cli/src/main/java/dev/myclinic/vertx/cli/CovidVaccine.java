package dev.myclinic.vertx.cli;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class CovidVaccine {

    private final static String CovidVaccineDir = System.getenv("COVID_VACCINE_DIR");

    public static void main(String[] args) throws Exception {
        if( args.length == 0 ){
            printHelp();
        } else {
            String cmd = args[0];
            switch (cmd) {
                case "backup-logbook": backupLogbook(); break;
                case "logbook-from-list": logbookFromList(); break;
                case "help": // fall through
                default:
                    printHelp();
                    break;
            }
        }
    }

    private static void printHelp(){
        System.out.println("CovidVaccine COMMAND [ARGS..]");
        System.out.println("  backup-logbook");
        System.out.println("  logbook-from-list");
        System.out.println("  help");
    }

    private static void logbookFromList() throws Exception {
        Path listFile = Path.of(CovidVaccineDir, "regular-patients.txt");
        if(Files.exists(listFile) ){
            List<String> lines = Misc.readLines(listFile.toString());
            List<RegularPatient> patients = lines.stream().map(CovidVaccine::parseRegularPatient)
                    .filter(Objects::nonNull).collect(toList());
            List<String> logs = new ArrayList<>();
            for(RegularPatient p: patients){
                logs.add(logbookAdd(p.patientId, p.name, p.age, p.phone));
                parsePatientAttr(p.attr);
                logs.add(logbookState(p.patientId, p.attr));
            }
            Path logbookPath = Path.of(CovidVaccineDir, "logbook.txt");
            if( Files.exists(logbookPath) ){
                System.err.printf("logbook (%s) already exists.\n", logbookPath.toString());
                System.exit(1);
            }
            Misc.saveLines(logbookPath.toString(), logs);
        } else {
            throw new FileNotFoundException(listFile.toString());
        }
    }

    private static String logbookAdd(int patientId, String name, int age, String phone){
        return String.format("ADD %d %s %d %s", patientId, name, age, phone);
    }

    private static String logbookState(int patientId, String attr){
        return String.format("STATE %d %s", patientId, attr);
    }

    private static void backupLogbook() throws Exception {
        Path logbookPath = Path.of(CovidVaccineDir, "logbook.txt");
        if( Files.exists(logbookPath) ){
            Path archDir = Path.of(CovidVaccineDir, "arch");
            Misc.ensureDirectory(archDir.toString());
            String stamp = Misc.makeTimestamp();
            Path backupPath = archDir.resolve(String.format("logbook-%s.txt", stamp));
            Files.move(logbookPath, backupPath);
            System.out.printf("%s moved to %s.\n ", logbookPath.toString(), backupPath.toString());
        } else {
            System.err.printf("logbook file (%s) does not exists.\n", logbookPath.toString());
        }
    }

    private interface PatientState {};

    private static class FirstShotCandidate implements PatientState {}

    private static class SecondShotCandidate implements PatientState {
        public static Pattern pat = Pattern.compile("S(\\d\\d)(\\d\\d)");
        public LocalDate firstShotDate;

        public SecondShotCandidate(LocalDate firstShotDate) {
            this.firstShotDate = firstShotDate;
        }
    }

    private static class NotCurrentCandidate implements PatientState {}

    private static class WaitingReply implements PatientState {}

    private static class NeedConfirm implements PatientState {}

    private static class DoneAtOtherPlace implements PatientState {}

    private static PatientState parsePatientAttr(String attr){
        attr = attr.trim();
        if( attr.length() > 0 ){
            switch (attr) {
                case "C":
                    return new FirstShotCandidate();
                case "x":
                    return new NotCurrentCandidate();
                case "P":
                    return new WaitingReply();
                case "*":
                    return new NeedConfirm();
                case "T":
                    return new DoneAtOtherPlace();
                default:
                    Matcher m = SecondShotCandidate.pat.matcher(attr);
                    if (m.matches()) {
                        int month = Integer.parseInt(m.group(1));
                        int day = Integer.parseInt(m.group(2));
                        int year = LocalDate.now().getYear();
                        return new SecondShotCandidate(LocalDate.of(year, month, day));
                    }
                    break;
            }
        }
        throw new RuntimeException("Invalid attribute: " + attr);
    }

    public static void mainOrig(String[] args) throws Exception {
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
            //String attr = parts[0];
            //rp.attr = attr.equals("*") ? "" : attr;
            rp.attr = parts[0];
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
            lines.add(String.format("(%d)", list.size()));
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

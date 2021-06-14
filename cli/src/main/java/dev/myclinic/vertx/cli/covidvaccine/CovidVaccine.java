package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.client2.Client;
import dev.myclinic.vertx.dto.PatientDTO;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class CovidVaccine {

    private final static String CovidVaccineDir = System.getenv("COVID_VACCINE_DIR");
    private final static Path logbookPath = Path.of(CovidVaccineDir, "logbook.txt");
    private final static Path patchPath = Path.of(CovidVaccineDir, "logbook-patch.txt");

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
        } else {
            String cmd = args[0];
            switch (cmd) {
                case "backup-logbook":
                    backupLogbook();
                    break;
                case "logbook-from-list":
                    logbookFromList();
                    break;
                case "prepare-patient-yomi":
                    preparePatientYomi();
                    break;
                case "list":
                    list();
                    break;
                case "due-second-shot":
                    dueSecondShot();
                    break;
                case "register-patch":
                    registerPatch(args);
                    break;
                case "list-patches":
                    listPatches();
                    break;
                case "apply-patches":
                    applyPatches();
                    break;
                case "current":
                    current();
                    break;
                case "patient-ids": {
                    patientIds();
                    break;
                }
                case "search": {
                    search(args);
                    break;
                }
                case "candidates-at": {
                    candidatesAt(args);
                    break;
                }
                case "list-appoint-dates": {
                    listAppointDates();
                    break;
                }
                case "appoint-acceptable": {
                    appointAcceptable(args);
                    break;
                }
                case "check-appoint-prefs": {
                    checkAppointPrefs();
                    break;
                }
                case "appoint-sheet": {
                    appointSheet(args);
                    break;
                }
                case "register-appoint": {
                    registerAppoint(args);
                    break;
                }
                case "appoints-at": {
                    appointsAt(args);
                    break;
                }
                case "add-patient": {
                    addPatient(args);
                    break;
                }
                case "help": // fall through
                default:
                    printHelp();
                    break;
            }
        }
    }

    private static void printHelp() {
        System.out.println("CovidVaccine COMMAND [ARGS..]");
        System.out.println("  backup-logbook");
        System.out.println("  logbook-from-list");
        System.out.println("  prepare-patient-yomi");
        System.out.println("  list");
        System.out.println("  due-second-shot");
        System.out.println("  register-patch ATTR PATIENT-ID");
        System.out.println("  list-patches");
        System.out.println("  apply-patches");
        System.out.println("  current");
        System.out.println("  patient-ids");
        System.out.println("  search SEARCH-TEXT");
        System.out.println("  candidates-at APPOINT-TIME");
        System.out.println("  list-appoint-dates");
        System.out.println("  appoint-acceptable PATIENT-ID APPOINT-TIME");
        System.out.println("  check-appoint-prefs");
        System.out.println("  appoint-sheet MM-DDThh:ss");
        System.out.println("  register-appoint APPOINT-TIME PATIENT-ID PATIENT-ID ...");
        System.out.println("  appoints-at APPOINT-TIME");
        System.out.println("  add-patient PATIENT-ID");
        System.out.println("  help");
    }

    private static void addPatient(String[] args) throws Exception {
        var params = new Object(){
            int patientId;
        };
        if( args.length == 2 ){
            params.patientId = Integer.parseInt(args[1]);
        } else {
            System.err.println("Invalid arg to add-patient");
            printHelp();
            System.exit(1);
        }
        Client client = Misc.getClient();
        PatientDTO patient = client.getPatient(params.patientId);
        RegularPatient regp = CovidMisc.patientToRegularPatient(patient);
        List<PatchCommand> patches = List.of(
                new PatchAdd(regp),
                new PatchState(regp.attr, patient.patientId));
        Map<Integer, RegularPatient> map = new HashMap<>();
        map.put(patient.patientId, regp);
        doApplyPatches(patches, map);
    }

    private static void appointsAt(String[] args){
        var params = new Object(){
            LocalDateTime at;
        };
        if( args.length == 2 ){
            params.at = parseAppointTime(args[1]);
        } else {
            System.err.println("Invalid arg to appoints-at");
            System.err.println("example -- appoints-at 06-19T14:00");
            System.exit(1);
        }
        List<RegularPatient> patients = getAppointsAt(params.at, executeLogbook());
        int index = 1;
        for(RegularPatient p: patients){
            System.out.printf("%d. %s\n", index++, p);
        }
    }

    static List<RegularPatient> getAppointsAt(LocalDateTime at, Collection<RegularPatient> patients){
        List<RegularPatient> result = new ArrayList<>();
        for(RegularPatient p: patients){
            PatientState state = parsePatientAttr(p.attr);
            if( state instanceof FirstShotAppoint ){
                if( at.equals(((FirstShotAppoint) state).at) ) {
                    result.add(p);
                }
            } else if( state instanceof SecondShotAppoint ){
                if( at.equals(((SecondShotAppoint) state).at) ){
                    result.add(p);
                }
            }
        }
        return result;
    }

    private static void registerAppoint(String[] args) throws Exception {
        var params = new Object(){
            LocalDateTime at;
            final List<Integer> patientIds = new ArrayList<>();
        };
        if( args.length > 1 ){
            params.at = parseAppointTime(args[1]);
            for(int i=2;i<args.length;i++){
                params.patientIds.add(Integer.parseInt(args[i]));
            }
        } else {
            System.err.println("Invalid arg to register-appoint.");
            System.err.println("example -- register-appoint APPOINT-TIME PATIENT-ID PATIENT-ID ...");
            System.exit(1);
        }
        Map<LocalDateTime, AppointDate> appointMap = readAppointDatesAsMap();
        AppointDate appDate = appointMap.get(params.at);
        if( appDate == null ){
            System.err.println("Invalid appoint date: " + params.at);
            System.exit(1);
        }
        Map<Integer, RegularPatient> patientMap = executeLogbookAsMap();
        List<RegularPatient> currentAppoints = getAppointsAt(params.at, patientMap.values());
        if( appDate.capacity < currentAppoints.size() + params.patientIds.size()){
            System.err.println("Overbooking!");
            System.exit(1);
        }
        Set<Integer> appointIds = new HashSet<>();
        appointIds.addAll(currentAppoints.stream().map(p -> p.patientId).collect(toList()));
        appointIds.addAll(params.patientIds);
        if( appointIds.size() != currentAppoints.size() + params.patientIds.size() ){
            System.err.println("Duplicate patient appointments.");
            System.exit(1);
        }
        List<PatchCommand> patches = new ArrayList<>();
        for(int patientId: params.patientIds){
            RegularPatient p = patientMap.get(patientId);
            if( p == null ){
                throw new RuntimeException("Unknown patient-id: " + patientId);
            }
            PatientState state = parsePatientAttr(p.attr);
            if( state instanceof Appointable ){
                PatientState newState = ((Appointable) state).registerAppoint(params.at);
                patches.add(new PatchState(newState.toString(), patientId));
            } else {
                System.err.printf("Cannot register appointment: %s\n", p);
                System.exit(1);
            }
        }
        doApplyPatches(patches, patientMap);
    }

    private static final Path appointPrefFile = Path.of(CovidVaccineDir, "appoint-pref.txt");

    private static class PatientAppointPref {
        public int patientId;
        public String name;
        public AppointPref appointPref;

        public PatientAppointPref(int patientId, String name, AppointPref appointPref) {
            this.patientId = patientId;
            this.name = name;
            this.appointPref = appointPref;
        }

        boolean acceptable(LocalDateTime at) {
            return appointPref.acceptable(at);
        }
    }

    private static Map<Integer, PatientAppointPref> readPatientAppointPrefs() throws Exception {
        Map<Integer, PatientAppointPref> map = new HashMap<>();
        for (String line : Misc.readLines(appointPrefFile)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("%")) {
                continue;
            }
            String[] items = line.split("\\s+", 3);
            int patientId = Integer.parseInt(items[0]);
            String name = items[1];
            AppointPref ap = AppointPref.parse(items[2]);
            if (map.containsKey(patientId)) {
                throw new RuntimeException("Duplicate appoint pref definition: " + patientId);
            }
            map.put(patientId, new PatientAppointPref(patientId, name, ap));
        }
        return map;
    }

    private static void checkAppointPrefs() throws Exception {
        Map<Integer, RegularPatient> patientMap = new HashMap<>();
        executeLogbook().forEach(p -> {
            patientMap.put(p.patientId, p);
        });
        Map<Integer, PatientAppointPref> prefMap = readPatientAppointPrefs();
        for (int patientId : prefMap.keySet()) {
            PatientAppointPref ap = prefMap.get(patientId);
            RegularPatient p = patientMap.get(patientId);
            if (p == null) {
                System.err.printf("No such patient: (%d) %s\n", patientId, ap.name);
            } else {
                if (!p.name.equals(ap.name)) {
                    System.err.printf("Name mismatch: (%d) %s -- %s\n",
                            patientId, ap.name, p.name);
                }
            }
        }
    }

    private static void appointAcceptable(String[] args) throws Exception {
        var params = new Object() {
            public int patientId;
            public LocalDateTime at;
        };
        if (args.length == 3) {
            params.patientId = Integer.parseInt(args[1]);
            params.at = parseAppointTime(args[2]);
        } else {
            System.err.println("Invalid arg to pref-dates-for.");
            System.err.println("example -- pref-dates-for 1234 2021-09-21T14:00");
            System.exit(1);
        }
        Map<Integer, PatientAppointPref> prefMap = readPatientAppointPrefs();
        PatientAppointPref pref = prefMap.get(params.patientId);
        if (pref == null || pref.acceptable(params.at)) {
            System.out.println("Acceptable");
        } else {
            System.out.println("Not acceptable");
        }
    }

    private static final Path appointDatesFile = Path.of(CovidVaccineDir, "appoint-dates.txt");

    private static class AppointDate {
        public LocalDateTime at;
        public int capacity;

        public AppointDate(LocalDateTime at, int capacity) {
            this.at = at;
            this.capacity = capacity;
        }

        private static final Pattern pat = Pattern.compile("^(\\d+-\\d+-\\d+)\\s+(\\d+):(\\d+)\\s+(\\d+)");

        public static AppointDate parse(String line) {
            Matcher m = pat.matcher(line);
            if (m.matches()) {
                LocalDateTime at = LocalDateTime.of(
                        LocalDate.parse(m.group(1)),
                        LocalTime.of(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))));
                return new AppointDate(
                        at,
                        Integer.parseInt(m.group(4))
                );
            } else {
                throw new RuntimeException("Invalid appoint date line: " + line);
            }
        }

        @Override
        public String toString() {
            return String.format("%s %02d時%02d分 %d名",
                    Misc.localDateToKanji(at.toLocalDate(), true, true),
                    at.getHour(),
                    at.getMinute(),
                    capacity);
        }
    }

    private static List<AppointDate> readAppointDates() {
        if (Files.exists(appointDatesFile)) {
            List<String> lines = Misc.readLines(appointDatesFile);
            return lines.stream()
                    .filter(s -> !s.isBlank())
                    .map(AppointDate::parse)
                    .collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    private static Map<LocalDateTime, AppointDate> readAppointDatesAsMap() {
        Map<LocalDateTime, AppointDate> map = new HashMap<>();
        readAppointDates().forEach(ap -> {
            if (map.containsKey(ap.at)) {
                throw new RuntimeException("Duplicate appoint dates: " + ap.at);
            }
            map.put(ap.at, ap);
        });
        return map;
    }

    private static void listAppointDates() throws Exception {
        readAppointDates().forEach(System.out::println);
    }

    private static boolean isCandidateAt(RegularPatient p, LocalDateTime at,
                                         Map<Integer, PatientAppointPref> prefMap) {
        PatientAppointPref ap = prefMap.get(p.patientId);
        if (ap != null && !ap.acceptable(at)) {
            return false;
        }
        PatientState state = parsePatientAttr(p.attr);
        if (state instanceof FirstShotCandidate) {
            return true;
        } else if (state instanceof SecondShotCandidate) {
            SecondShotCandidate sc = (SecondShotCandidate) state;
            LocalDate dueDate = sc.firstShotDate.plus(21, ChronoUnit.DAYS);
            return dueDate.isEqual(at.toLocalDate()) || dueDate.isBefore(at.toLocalDate());
        } else {
            return false;
        }
    }

    private static List<RegularPatient> listCandidates(List<RegularPatient> logbook,
                                                       LocalDateTime at,
                                                       Map<Integer, PatientAppointPref> prefMap,
                                                       int n) {
        List<RegularPatient> candidates = logbook
                .stream()
                .filter(p -> isCandidateAt(p, at, prefMap))
                .collect(toList());
        Collections.shuffle(candidates);
        return candidates.subList(0, Math.min(n, candidates.size()));
    }

    private static void candidatesAt(String[] args) throws Exception {
        var params = new Object() {
            public LocalDateTime at;
        };
        if (args.length == 2) {
            params.at = parseAppointTime(args[1]);
        } else {
            System.err.println("Invalid arg to candidates-at");
            System.err.println("exmaple -- candidates-at 2021-06-19T14:00");
            System.exit(1);
        }
        Map<Integer, PatientAppointPref> prefMap = readPatientAppointPrefs();
        List<RegularPatient> candidates = listCandidates(executeLogbook(), params.at, prefMap, 30);
        candidates.forEach(System.out::println);
    }

    private static void appointSheet(String[] args) throws Exception {
        var params = new Object() {
            LocalDateTime at;
        };
        if (args.length == 2) {
            params.at = parseAppointTime(args[1]);
        } else {
            System.err.println("Invalid arg to appoint-sheet.");
            System.err.println("example -- appoint-sheet 06-19T14:00");
            System.exit(1);
        }
        Map<LocalDateTime, AppointDate> appointMap = readAppointDatesAsMap();
        AppointDate ap = appointMap.get(params.at);
        if (ap == null) {
            System.err.printf("Not an appoint date: %s", params.at);
            System.exit(1);
        } else {
            System.out.printf("%s\n", appointTimeRep(params.at));
            System.out.println();
            List<RegularPatient> logbook = executeLogbook();
            List<RegularPatient> patients = logbook.stream().filter(p -> {
                PatientState state = parsePatientAttr(p.attr);
                if (state instanceof FirstShotAppoint) {
                    FirstShotAppoint fsa = (FirstShotAppoint) state;
                    return fsa.at.equals(params.at);
                } else if (state instanceof SecondShotAppoint) {
                    SecondShotAppoint ssa = (SecondShotAppoint) state;
                    return ssa.at.equals(params.at);
                } else {
                    return false;
                }
            }).collect(toList());
            var locals = new Object() {
                int index = 1;
            };
            int capacity = ap.capacity;
            patients.forEach(p -> System.out.printf("%d. %s\n", locals.index++, p));
            for (int i = locals.index; i <= capacity; i++) {
                System.out.printf("%d. \n", i);
            }
            System.out.println();
            if (capacity < patients.size()) {
                System.err.println("Overbooking!");
                System.exit(1);
            } else if (capacity > patients.size()) {
                Map<Integer, PatientAppointPref> prefMap = readPatientAppointPrefs();
                List<RegularPatient> candidates = listCandidates(logbook, params.at, prefMap, 30);
                candidates.forEach(System.out::println);
            }
        }
    }

    private static void search(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Invalid arguments to search command.");
            printHelp();
            System.exit(1);
        } else {
            String text = args[1];
            List<RegularPatient> patients = executeLogbook();
            List<RegularPatient> results = new ArrayList<>();
            try {
                int patientId = Integer.parseInt(text);
                for (RegularPatient p : patients) {
                    if (p.patientId == patientId) {
                        results.add(p);
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                for (RegularPatient p : patients) {
                    if (p.name.contains(text)) {
                        results.add(p);
                    }
                }
            }
            if (results.size() == 0) {
                System.out.println("(No result)");
            } else {
                for (RegularPatient p : results) {
                    System.out.println(p.toString());
                }
            }
        }
    }

    private static boolean doApplyPatches(List<PatchCommand> patches, Map<Integer, RegularPatient> patientMap)
            throws Exception {
        for(PatchCommand patch: patches){
            if( patch instanceof PatchAdd ){
                PatchAdd patchAdd = (PatchAdd) patch;
                System.out.printf("ADD %s\n", patchAdd.patient);
            } else if( patch instanceof PatchState ){
                PatchState patchState = (PatchState) patch;
                RegularPatient patient = patientMap.get(patchState.patientId).copy();
                patient.attr = patchState.attr;
                System.out.printf("STATE %s\n", patient);
            } else {
                throw new RuntimeException("Unknown patch: " + patch);
            }
        }
        System.console().writer().print("Apply these patches? (Y/N) ");
        System.console().writer().flush();
        String input = System.console().readLine();
        if (input.startsWith("Y")) {
            List<String> patchLines = patches.stream().map(PatchCommand::encode).collect(toList());
            Misc.appendLines(logbookPath.toString(), patchLines);
            return true;
        } else {
            return false;
        }
    }

    private static void applyPatches() throws Exception {
        List<RegularPatient> patients = executeLogbook();
        Map<Integer, RegularPatient> map = new HashMap<>();
        patients.forEach(p -> map.put(p.patientId, p));
        List<String> patchLines = Misc.readLines(patchPath.toString());
        for (String patchLine : patchLines) {
            PatchCommand cmd = parsePatch(patchLine);
            if (cmd instanceof PatchAdd) {
                PatchAdd pa = (PatchAdd) cmd;
                System.out.printf("ADD %s\n", pa.patient.toString());
            } else if (cmd instanceof PatchState) {
                PatchState ps = (PatchState) cmd;
                RegularPatient rp = map.get(ps.patientId);
                if (rp == null) {
                    throw new RuntimeException("Cannot find patient with patient-id: " + ps.patientId);
                }
                rp.attr = ps.attr;
                System.out.printf("STATE %s\n", rp.toString());
            } else {
                throw new RuntimeException("Unknown patch: " + patchLine);
            }
        }
        System.console().writer().print("Apply these patches? (Y/N) ");
        System.console().writer().flush();
        String input = System.console().readLine();
        if (input.startsWith("Y")) {
            List<String> logs = Misc.readLines(logbookPath.toString());
            logs.addAll(patchLines);
            executeLogbook(logs);
            Misc.appendLines(logbookPath.toString(), patchLines);
            Path archDir = Path.of(CovidVaccineDir, "arch");
            Misc.ensureDirectory(archDir);
            Misc.backupFile(patchPath, archDir, s -> s + "-done");
        }
    }

    private static void listPatches() throws Exception {
        if (Files.exists(patchPath)) {
            List<String> lines = Misc.readLines(patchPath.toString());
            lines.forEach(System.out::println);
        }
    }

    private static void registerPatch(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println();
            System.err.print("Invalid patch: ");
            for (int i = 1; i < args.length; i++) {
                System.err.print(args[i]);
                System.err.print(" ");
            }
            System.err.println();
            System.exit(1);
        }
        String attr = args[1];
        int patientId = Integer.parseInt(args[2]);
        parsePatientAttr(attr);
        String patch = logbookState(patientId, attr);
        Misc.appendLines(patchPath.toString(), Collections.singletonList(patch));
    }

    private static void dueSecondShot() throws Exception {
        List<RegularPatient> patients = executeLogbook();
        Map<LocalDate, List<RegularPatient>> map = new HashMap<>();
        for (RegularPatient patient : patients) {
            PatientState st = parsePatientAttr(patient.attr);
            if (st instanceof SecondShotCandidate) {
                SecondShotCandidate ssc = (SecondShotCandidate) st;
                LocalDate dueDate = ssc.firstShotDate.plus(3, ChronoUnit.WEEKS);
                List<RegularPatient> list = map.computeIfAbsent(dueDate, k -> new ArrayList<>());
                list.add(patient);
            }
        }
        List<LocalDate> dates = new ArrayList<>(map.keySet());
        dates.sort(Comparator.naturalOrder());
        Map<Integer, String> yomiMap = readPatientYomi();
        for (LocalDate d : dates) {
            List<RegularPatient> list = map.get(d);
            list.sort(Comparator.comparing(p -> getPatientYomi(p.patientId, yomiMap)));
            list.forEach(p -> {
                String s = String.format("%02d/%02d（%s） %04d %s", d.getMonthValue(), d.getDayOfMonth(),
                        Misc.youbiIndexToKanji(d.getDayOfWeek().getValue()),
                        p.patientId, p.name);
                System.out.println(s);
            });
            System.out.println();
        }
    }

    private static void sortPatients(List<RegularPatient> list) throws Exception {
        Map<Integer, String> yomiMap = readPatientYomi();
        list.sort(Comparator.comparing(p -> getPatientYomi(p.patientId, yomiMap)));
    }

    private static void logbookFromList() throws Exception {
        Path listFile = Path.of(CovidVaccineDir, "regular-patients.txt");
        if (Files.exists(listFile)) {
            List<String> lines = Misc.readLines(listFile.toString());
            List<RegularPatient> patients = lines.stream().map(CovidVaccine::parseRegularPatient)
                    .filter(Objects::nonNull).collect(toList());
            List<String> logs = new ArrayList<>();
            for (RegularPatient p : patients) {
                logs.add(logbookAdd(p.patientId, p.name, p.age, p.phone));
                parsePatientAttr(p.attr);
                if (p.attr.equals("*") && p.age < 65) {
                    p.attr = "U";
                }
                logs.add(logbookState(p.patientId, p.attr));
            }
            if (Files.exists(logbookPath)) {
                System.err.printf("logbook (%s) already exists.\n", logbookPath.toString());
                System.exit(1);
            }
            Misc.saveLines(logbookPath.toString(), logs);
        } else {
            throw new FileNotFoundException(listFile.toString());
        }
    }

    private static String logbookAdd(int patientId, String name, int age, String phone) {
        return String.format("ADD %d %s %d %s", patientId, name, age, phone);
    }

    private static String logbookState(int patientId, String attr) {
        return String.format("STATE %d %s", patientId, attr);
    }

    private static void backupLogbook() throws Exception {
        if (Files.exists(logbookPath)) {
            Path archDir = Path.of(CovidVaccineDir, "arch");
            Misc.ensureDirectory(archDir.toString());
            String stamp = Misc.makeTimestamp();
            Path backupPath = archDir.resolve(String.format("logbook-%s.txt", stamp));
            Files.copy(logbookPath, backupPath);
            System.out.printf("%s copied to %s.\n ", logbookPath.toString(), backupPath.toString());
        } else {
            System.err.printf("logbook file (%s) does not exists.\n", logbookPath.toString());
        }
    }

    private static void preparePatientYomi() throws Exception {
        String server = System.getenv("MYCLINIC_REMOTE_SERVICE");
        if (server == null) {
            throw new RuntimeException("Cannot find env var: MYCLINIC_REMOTE_SERVICE");
        }
        List<RegularPatient> patients = executeLogbook();
        List<String> lines = new ArrayList<>();
        Client client = new Client(server);
        for (RegularPatient rp : patients) {
            PatientDTO p = client.getPatient(rp.patientId);
            lines.add(String.format("%d %s %s", rp.patientId, p.lastNameYomi, p.firstNameYomi));
        }
        Path yomiFile = Path.of(CovidVaccineDir, "patient-yomi.txt");
        Misc.saveLines(yomiFile.toString(), lines);
    }

    private static Map<Integer, String> readPatientYomi() throws Exception {
        Path yomiFile = Path.of(CovidVaccineDir, "patient-yomi.txt");
        Map<Integer, String> map = new HashMap<>();
        Misc.readLines(yomiFile.toString()).forEach(line -> {
            line = line.trim();
            if (line.isEmpty()) {
                return;
            }
            String[] parts = line.split("\\s+", 2);
            if (parts.length != 2) {
                throw new RuntimeException("Invalid patient-yomi line: " + line);
            }
            int patientId = Integer.parseInt(parts[0]);
            String yomi = parts[1];
            map.put(patientId, yomi);
        });
        return map;
    }

    private static String getPatientYomi(int patientId, Map<Integer, String> map) {
        if (map.containsKey(patientId)) {
            return map.get(patientId);
        } else {
            String server = System.getenv("MYCLINIC_REMOTE_SERVICE");
            if (server == null) {
                throw new RuntimeException("Cannot find env var: MYCLINIC_REMOTE_SERVICE");
            }
            Client client = new Client(server);
            PatientDTO patient = client.getPatient(patientId);
            return String.format("%s %s", patient.lastNameYomi, patient.firstNameYomi);
        }
    }

    private interface PatchCommand {
        String encode();
    }

    private static class PatchAdd implements PatchCommand {
        public RegularPatient patient;

        public PatchAdd(RegularPatient patient) {
            this.patient = patient;
        }

        @Override
        public String encode() {
            return String.format("ADD %d %s %d %s",
                    patient.patientId, patient.name, patient.age, patient.phone);
        }
    }

    private static class PatchState implements PatchCommand {
        public String attr;
        public int patientId;

        public PatchState(String attr, int patientId) {
            this.attr = attr;
            this.patientId = patientId;
            parsePatientAttr(attr);
        }

        @Override
        public String encode() {
            return String.format("STATE %d %s", patientId, attr);
        }
    }

    private static PatchCommand parsePatch(String patch) {
        patch = patch.trim();
        String[] parts = patch.split("\\s+", 2);
        if (parts.length != 2) {
            throw new RuntimeException("Invalid log/patch: " + patch);
        }
        String cmd = parts[0];
        switch (cmd) {
            case "ADD": {
                String[] items = parts[1].split("\\s+", 4);
                if (items.length != 4) {
                    throw new RuntimeException("Invalid log: " + patch);
                }
                RegularPatient p = new RegularPatient();
                p.patientId = Integer.parseInt(items[0]);
                p.name = items[1];
                p.age = Integer.parseInt(items[2]);
                p.phone = items[3];
                p.attr = p.age >= 65 ? "*" : "U";
                return new PatchAdd(p);
            }
            case "STATE": {
                String[] items = parts[1].split("\\s+", 2);
                if (items.length != 2) {
                    throw new RuntimeException("Invalid log: " + patch);
                }
                int patientId = Integer.parseInt(items[0]);
                String attr = items[1];
                parsePatientAttr(attr);
                return new PatchState(attr, patientId);
            }
            default: {
                throw new RuntimeException("Invalid command: " + patch);
            }
        }
    }

    private static List<RegularPatient> executeLogbook() {
        List<String> logs = Misc.readLines(logbookPath.toString());
        return executeLogbook(logs);
    }

    private static List<RegularPatient> executeLogbook(List<String> logs) {
        Map<Integer, RegularPatient> map = new HashMap<>();
        for (String log : logs) {
            log = log.trim();
            if (log.isEmpty()) {
                continue;
            }
            PatchCommand cmd = parsePatch(log);
            if (cmd instanceof PatchAdd) {
                PatchAdd pa = (PatchAdd) cmd;
                RegularPatient p = pa.patient;
                if (map.containsKey(p.patientId)) {
                    throw new RuntimeException("Duplicate ADD command: " + log);
                }
                map.put(p.patientId, p);
            } else if (cmd instanceof PatchState) {
                PatchState ps = (PatchState) cmd;
                int patientId = ps.patientId;
                String attr = ps.attr;
                parsePatientAttr(attr);
                RegularPatient patient = map.get(patientId);
                if (patient == null) {
                    throw new RuntimeException("Invalid patient-id: " + String.format("%d", patientId));
                }
                patient.attr = attr;
            } else {
                throw new RuntimeException("Unknown patch: " + log);
            }
        }
        return new ArrayList<>(map.values());
    }

    static Map<Integer, RegularPatient> executeLogbookAsMap(){
        Map<Integer, RegularPatient> map = new HashMap<>();
        executeLogbook().forEach(p -> {
            if( map.containsKey(p.patientId) ){
                throw new RuntimeException("Duplicate patient-ids in logbook.");
            }
            map.put(p.patientId, p);
        });
        return map;
    }

    private static Map<String, List<RegularPatient>> prepareGroupsForListing() throws Exception {
        List<RegularPatient> patients = executeLogbook();
        Map<String, List<RegularPatient>> groups = new HashMap<>();
        patients.forEach(p -> {
            String code = p.attr.substring(0, 1);
            List<RegularPatient> list = groups.computeIfAbsent(code, k -> new ArrayList<>());
            list.add(p);
        });
        List<String> codes = new ArrayList<>();
        Set<String> keys = new HashSet<>(groups.keySet());
        List.of("C", "S", "*", "P", "T").forEach(c -> {
            codes.add(c);
            keys.remove(c);
        });
        keys.remove("x");
        keys.remove("U");
        codes.addAll(keys);
        Map<String, List<RegularPatient>> result = new LinkedHashMap<>();
        for (String code : codes) {
            result.put(code, groups.get(code));
        }
        return result;
    }


    private static List<String> prepareList() throws Exception {
        Map<String, List<RegularPatient>> groups = prepareGroupsForListing();
        Map<Integer, String> yomiMap = readPatientYomi();
        List<String> lines = new ArrayList<>();
        for (String code : groups.keySet()) {
            List<RegularPatient> list = groups.getOrDefault(code, Collections.emptyList());
            list.sort(Comparator.comparing(p -> getPatientYomi(p.patientId, yomiMap)));
            list.forEach(p -> lines.add(p.toString()));
            lines.add(String.format("(%d)", list.size()));
            lines.add("");
        }
        return lines;
    }

    private static void list() throws Exception {
        List<String> lines = prepareList();
        lines.forEach(System.out::println);
    }

    private static void current() throws Exception {
        List<String> lines = prepareList();
        Path currentPath = Path.of(CovidVaccineDir, "current.txt");
        Misc.saveLines(currentPath.toString(), lines);
    }

    private static void patientIds() throws Exception {
        Map<String, List<RegularPatient>> groups = prepareGroupsForListing();
        for (String code : groups.keySet()) {
            List<RegularPatient> list = groups.computeIfAbsent(code, k -> Collections.emptyList());
            List<String> patientIds = list.stream()
                    .sorted(Comparator.comparing(p -> p.patientId))
                    .map(p -> String.format("%d", p.patientId))
                    .collect(toList());
            System.out.println(code);
            System.out.println(String.join(", ", patientIds));
            System.out.println();
        }
    }

    private interface PatientState {
    }

    interface Appointable {
        PatientState registerAppoint(LocalDateTime at);
    }

    private static class FirstShotCandidate implements PatientState, Appointable {
        @Override
        public PatientState registerAppoint(LocalDateTime at) {
            return new FirstShotAppoint(at);
        }
    }

    private static class SecondShotCandidate implements PatientState, Appointable {
        public static Pattern pat = Pattern.compile("S(\\d\\d)(\\d\\d)");
        public LocalDate firstShotDate;

        public SecondShotCandidate(LocalDate firstShotDate) {
            this.firstShotDate = firstShotDate;
        }

        @Override
        public PatientState registerAppoint(LocalDateTime at) {
            LocalDate dueDate = firstShotDate.plus(21, ChronoUnit.DAYS);
            if( at.toLocalDate().isBefore(dueDate) ){
                throw new RuntimeException("Too early second shot appointment.");
            }
            return new SecondShotAppoint(at);
        }
    }

    private static class NotCurrentCandidate implements PatientState {
    }

    private static class WaitingReply implements PatientState {
    }

    private static class NeedConfirm implements PatientState {
    }

    private static class DoneAtOtherPlace implements PatientState {
    }

    private static class Under65 implements PatientState {
    }

    private static class FirstShotAppoint implements PatientState {
        public static Pattern pat = Pattern.compile("A(\\d+-\\d+-\\d+)T(\\d+):(\\d+)");
        public LocalDateTime at;

        public FirstShotAppoint(LocalDateTime at) {
            this.at = at;
        }

        @Override
        public String toString(){
            return "A" + encodeAppointTime(at);
        }
    }

    private static class SecondShotAppoint implements PatientState {
        public static Pattern pat = Pattern.compile("B(\\d+-\\d+-\\d+)T(\\d+):(\\d+)");
        public LocalDateTime at;

        public SecondShotAppoint(LocalDateTime at) {
            this.at = at;
        }

        @Override
        public String toString(){
            return "B" + encodeAppointTime(at);
        }
    }

    private static PatientState parsePatientAttr(String attr) {
        attr = attr.trim();
        if (attr.length() > 0) {
            switch (attr.charAt(0)) {
                case 'C':
                    return new FirstShotCandidate();
                case 'x':
                    return new NotCurrentCandidate();
                case 'P':
                    return new WaitingReply();
                case '*':
                    return new NeedConfirm();
                case 'T':
                    return new DoneAtOtherPlace();
                case 'U':
                    return new Under65();
                case 'S': {
                    Matcher m = SecondShotCandidate.pat.matcher(attr);
                    if (m.matches()) {
                        int month = Integer.parseInt(m.group(1));
                        int day = Integer.parseInt(m.group(2));
                        int year = LocalDate.now().getYear();
                        return new SecondShotCandidate(LocalDate.of(year, month, day));
                    }
                    break;
                }
                case 'A': {
                    Matcher m = FirstShotAppoint.pat.matcher(attr);
                    if (m.matches()) {
                        LocalDateTime at = LocalDateTime.of(
                                LocalDate.parse(m.group(1)),
                                LocalTime.of(
                                        Integer.parseInt(m.group(2)),
                                        Integer.parseInt(m.group(3)))
                        );
                        return new FirstShotAppoint(at);
                    }
                    break;
                }
                case 'B': {
                    Matcher m = SecondShotAppoint.pat.matcher(attr);
                    if (m.matches()) {
                        LocalDateTime at = LocalDateTime.of(
                                LocalDate.parse(m.group(1)),
                                LocalTime.of(
                                        Integer.parseInt(m.group(2)),
                                        Integer.parseInt(m.group(3)))
                        );
                        return new SecondShotAppoint(at);
                    }
                    break;
                }
            }
        }
        throw new RuntimeException("Invalid attribute: " + attr);
    }

    private static final Pattern patAppointTime = Pattern.compile("(\\d+-\\d+-\\d+)T(\\d+):(\\d+)");
    private static final Pattern patAppointTime2 = Pattern.compile("(\\d+)-(\\d+)T(\\d+):(\\d+)");

    static LocalDateTime parseAppointTime(String src) {
        Matcher m = patAppointTime.matcher(src);
        if (m.matches()) {
            return LocalDateTime.of(
                    LocalDate.parse(m.group(1)),
                    LocalTime.of(
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)))
            );
        }
        m = patAppointTime2.matcher(src);
        if (m.matches()) {
            return LocalDateTime.of(
                    LocalDate.of(LocalDate.now().getYear(),
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2))),
                    LocalTime.of(
                            Integer.parseInt(m.group(3)),
                            Integer.parseInt(m.group(4)))
            );
        }
        throw new RuntimeException("Cannot parse appoint time: " + src);
    }

    static String encodeAppointTime(LocalDateTime at){
        return String.format("%d-%02d-%02dT%02d:%02d", at.getYear(), at.getMonthValue(), at.getDayOfMonth(),
                at.getHour(), at.getMinute());
    }

    static String appointTimeRep(LocalDateTime at) {
        return String.format("%s %02d時%02d分",
                Misc.localDateToKanji(at.toLocalDate(), false, true),
                at.getHour(), at.getMinute());
    }


    static class RegularPatient {
        int patientId;
        String name;
        int age;
        String phone;
        String attr;

        public RegularPatient(int patientId, String name, int age, String phone, String attr) {
            this.patientId = patientId;
            this.name = name;
            this.age = age;
            this.phone = phone;
            this.attr = attr;
        }

        RegularPatient(){

        }

        public boolean isExcluded() {
            return attr.startsWith("x") || attr.startsWith("X");
        }

        public String toStringWithoutAttr(){
            return String.format("%d %s %d才 %s", patientId, name, age, phone);
        }

        @Override
        public String toString() {
            return attr + " " + toStringWithoutAttr();
        }

        RegularPatient copy(){
            RegularPatient c = new RegularPatient();
            c.patientId = patientId;
            c.name = name;
            c.age = age;
            c.phone = phone;
            c.attr = attr;
            return c;
        }
    }

    private static RegularPatient parseRegularPatient(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        } else {
            RegularPatient rp = new RegularPatient();
            char ch = line.charAt(0);
            if (Character.isDigit(ch)) {
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

}

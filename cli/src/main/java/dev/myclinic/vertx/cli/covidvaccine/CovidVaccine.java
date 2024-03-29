package dev.myclinic.vertx.cli.covidvaccine;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.cli.covidvaccine.appointslot.AppointSlot;
import dev.myclinic.vertx.cli.covidvaccine.appointslot.FirstShotSlot;
import dev.myclinic.vertx.cli.covidvaccine.appointslot.SecondShotSlot;
import dev.myclinic.vertx.cli.covidvaccine.logentry.LogEntry;
import dev.myclinic.vertx.cli.covidvaccine.logentry.StateLog;
import dev.myclinic.vertx.cli.covidvaccine.patientevent.*;
import dev.myclinic.vertx.client2.Client;
import dev.myclinic.vertx.drawer.form.Form;
import dev.myclinic.vertx.drawer.form.Page;
import dev.myclinic.vertx.drawer.pdf.PdfPrinter;
import dev.myclinic.vertx.dto.ClinicInfoDTO;
import dev.myclinic.vertx.dto.PatientDTO;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class CovidVaccine {

    private final static String CovidVaccineDir = System.getenv("COVID_VACCINE_DIR");
    private final static Path logbookPath = Path.of(CovidVaccineDir, "logbook.txt");
    private final static Path patchPath = Path.of(CovidVaccineDir, "logbook-patch.txt");
    private final static AppointBook book = new AppointBook(CovidVaccineDir);

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
                    list(args);
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
                    patientIds(args);
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
                case "batch-update-phone": {
                    batchUpdatePhone();
                    break;
                }
                case "random": {
                    pickRandom(args);
                    break;
                }
                case "calendar": {
                    calendar(args);
                    break;
                }
                case "calendar-2nd": {
                    calendar2nd(args);
                    break;
                }
                case "calendar-2nd-list": {
                    calendar2ndList(args);
                    break;
                }
                case "history": {
                    history(args);
                    break;
                }
                case "register-ephemeral-second-appoint": {
                    registerEphemeralSecondAppoint(args);
                    break;
                }
                case "batch-register-ephemeral-second-appoint": {
                    batchRegisterEphemeralSecondAppoint();
                    break;
                }
                case "batch-convert-u-to-k": {
                    batchConvertUtoK();
                    break;
                }
                case "patch": {
                    patch(args);
                    break;
                }
                case "injection-done": {
                    injectionDone(args);
                    break;
                }
                case "ephemeral-to-real": {
                    ephemeralToReal(args);
                    break;
                }
                case "remaining": {
                    remaining();
                    break;
                }
                case "print-2nd-shot-appoints": {
                    printSecondShotAppoints(args);
                    break;
                }
                case "vial-balance": {
                    vialBalance(args);
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
        System.out.println("  patient-ids CANDIDATE-STATE");
        System.out.println("  search SEARCH-TEXT");
        System.out.println("  candidates-at APPOINT-TIME");
        System.out.println("  list-appoint-dates");
        System.out.println("  appoint-acceptable PATIENT-ID APPOINT-TIME");
        System.out.println("  check-appoint-prefs");
        System.out.println("  appoint-sheet MM-DDThh:ss");
        System.out.println("  register-appoint APPOINT-TIME PATIENT-ID PATIENT-ID ...");
        System.out.println("  appoints-at APPOINT-TIME");
        System.out.println("  add-patient PATIENT-ID");
        System.out.println("  batch-update-phone");
        System.out.println("  random MIN MAX");
        System.out.println("  calendar [APPOINT-DATE]");
        System.out.println("  calendar-2nd");
        System.out.println("  history PATIENT-ID");
        System.out.println("  register-ephemeral-second-appoint PATIENT-ID PATIENT-ID ...");
        System.out.println("  batch-register-ephemeral-second-appoint");
        System.out.println("  batch-convert-u-to-k");
        System.out.println("  patch ATTR PATIENT-ID PATIENT-ID ...");
        System.out.println("  injection-done APPOINT-TIME");
        System.out.println("  ephemeral-to-real PATIENT-ID ...");
        System.out.println("  print-2nd-shot-appoints APPOINT-TIME");
        System.out.println("  vial-balance [APPOINT-TIME+=N|SUPPLY-DATE=N]...");
        System.out.println("  help");
    }

    private static VialDelivered tryParseVialArg(String s) {
        int index = s.indexOf("=");
        if (index < 0) {
            return null;
        }
        String dateArg = s.substring(0, index);
        String nArg = s.substring(index + 1);
        LocalDate date = CovidMisc.tryParseDate(dateArg);
        if (date != null) {
            try {
                int n = Integer.parseInt(nArg);
                return new VialDelivered(date, n);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private static class DummyAppoint {
        public LocalDateTime at;
        public int n;

        public DummyAppoint(LocalDateTime at, int n) {
            this.at = at;
            this.n = n;
        }

        static DummyAppoint tryParse(String str) {
            int i = str.indexOf("+=");
            if (i < 0) {
                throw new RuntimeException("Invalid dummy appoint: " + str);
            }
            String left = str.substring(0, i);
            String right = str.substring(i + 2);
            LocalDateTime at = tryParseAppointTime(left);
            if (at != null) {
                try {
                    int n = Integer.parseInt(right);
                    return new DummyAppoint(at, n);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private static List<LogEntry> dummyAppoint(LocalDateTime at, int n) {
        List<LogEntry> entries = new ArrayList<>();
        LocalDate due = at.toLocalDate().plus(21, ChronoUnit.DAYS);
        for (int i = 0; i < n; i++) {
            StateLog log = new StateLog(0, new FirstShotAppoint(at));
            entries.add(log);
            LocalDateTime at2 = book.findVacancy(due, dt -> true);
            if (at2 == null) {
                throw new RuntimeException("Cannot find 2nd shot ephemeral. " + at);
            }
            StateLog log2 = new StateLog(0, new EphemeralSecondShotAppoint(at2));
            entries.add(log2);
        }
        return entries;
    }

    private static void vialBalance(String[] args) throws Exception {
        Path supplyFile = Path.of(CovidVaccineDir, "supply.txt");
        List<VialDelivered> vialDelivereds = Misc.parseLines(supplyFile, VialDelivered::parse);
        book.readLogs();
        int dummyPatientId = -1;
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            VialDelivered vialDelivered = tryParseVialArg(arg);
            if (vialDelivered != null) {
                vialDelivereds.add(vialDelivered);
                System.out.println(vialDelivered);
                continue;
            }
            DummyAppoint dummyAppoint = DummyAppoint.tryParse(arg);
            if (dummyAppoint != null) {
                for (int j = 0; j < dummyAppoint.n; j++) {
                    int patientId = dummyPatientId--;
                    StateLog log1 = new StateLog(patientId, new FirstShotAppoint(dummyAppoint.at));
                    book.applyLogEntry(log1);
                    LocalDateTime at2 = book.findVacancy(dummyAppoint.at.toLocalDate().plus(21, ChronoUnit.DAYS),
                            dt -> true);
                    if (at2 == null) {
                        throw new RuntimeException("Cannot find 2nd shot for: " + dummyAppoint.at);
                    }
                    StateLog log2 = new StateLog(patientId, new EphemeralSecondShotAppoint(at2));
                    book.applyLogEntry(log2);
                    System.out.printf("[Dummy Appoint] %s -- %s\n", appointTimeRep(dummyAppoint.at), appointTimeRep(at2));
                }
                continue;
            }
            throw new RuntimeException("Invalid arg: " + arg);
        }
        book.checkOverbooking();
        for (LocalDateTime at : book.listAppointTime()) {
            AppointDate appointDate = book.getAppointDate(at);
            AppointBlock block = book.getAppointBlock(at);
            if (appointDate.capacity > 0) {
                int capacity = appointDate.capacity;
                int apps = block.slots.size();
                int req = (apps + 5) / 6;
                int available = VialDelivered.availableAt(vialDelivereds, at.toLocalDate());
                int balance = available - req;
                if (req > 0) {
                    VialDelivered.consume(vialDelivereds, at.toLocalDate(), req);
                }
                System.out.printf("%s %d/%d %d\n",
                        appointTimeRep(at),
                        apps,
                        capacity,
                        balance);
            }
        }
    }

    private static void printSecondShotAppoints(String[] args) throws Exception {
        LocalDateTime at = null;
        if (args.length == 2) {
            at = parseAppointTime(args[1]);
        } else {
            System.err.println("Invalid args to print-2nd-shot-appoints");
            printHelp();
            System.exit(1);
        }
        URL url = CovidVaccine.class.getClassLoader().getResource("covidvaccine/covid-vac-2nd-shot.json");
        ObjectMapper mapper = Misc.createObjectMapper();
        Form form = mapper.readValue(url, Form.class);
        List<Page> pages = new ArrayList<>();
        Page origPage = form.pages.get(0);
        form.pages = pages;
        PdfPrinter printer = new PdfPrinter(form.paper);
        List<PdfPrinter.FormPageData> dataList = new ArrayList<>();
        Client client = Misc.getClient();
        ClinicInfoDTO clinicInfo = client.getClinicInfo();
        AppointBlock block = book.getAppointBlock(at);
        int pageId = 0;
        for (AppointSlot slot : block.slots) {
            if (slot instanceof FirstShotSlot) {
                int patientId = slot.patientId;
                Patient patient = book.getPatient(patientId);
                PatientState ps = book.getPatientState(patientId);
                if (ps.secondShotState == SecondShotState.Ephemeral ||
                        ps.secondShotState == SecondShotState.Appointed) {
                    pages.add(origPage);
                    LocalDateTime secondAt = ps.secondShotTime;
                    PdfPrinter.FormPageData pageData = new PdfPrinter.FormPageData();
                    Map<String, String> marks = new HashMap<>();
                    marks.put("patient", String.format("%s 様", patient.name));
                    marks.put("at", appointTimeRep(secondAt));
                    marks.put("postal-code", clinicInfo.postalCode);
                    marks.put("address", clinicInfo.address);
                    marks.put("phone", clinicInfo.tel);
                    marks.put("clinic-name", clinicInfo.name);
                    marks.put("doctor", clinicInfo.doctorName);
                    pageData.pageId = pageId++;
                    pageData.markTexts = marks;
                    pageData.customRenderers = new HashMap<>();
                    dataList.add(pageData);
                }
            }
        }
        if (dataList.size() > 0) {
            String file = String.format("second-shot-appoints-%d%02d%02d-%02d%02d.pdf",
                    at.getYear(), at.getMonthValue(), at.getDayOfMonth(),
                    at.getHour(), at.getMinute());
            try (OutputStream os = new FileOutputStream("work" + "/" + file)) {
                printer.print(form, dataList, os);
            }
            System.out.printf("Written to work/%s\n", file);
        } else {
            System.out.println("No output");
        }
    }

    private static void remaining() throws Exception {
        List<Integer> patientIds = book.listPatientId();
        int count = 0;
        for (int patientId : patientIds) {
            PatientState ps = book.getPatientState(patientId);
            if (ps.firstShotTime == null && ps.candidateEvent != null) {
                count++;
                Patient patient = book.getPatient(patientId);
                System.out.printf("%s (%d) %s\n", ps.candidateEvent.encode(), patientId, patient.name);
            }
        }
        System.out.printf("(%d)\n", count);
    }

    private static void ephemeralToReal(String[] args) throws Exception {
        List<Integer> patientIds = new ArrayList<>();
        if (args.length >= 2) {
            for (int i = 1; i < args.length; i++) {
                patientIds.add(Integer.parseInt(args[i]));
            }
        } else {
            System.err.println("Invalid args to ephemeral-to-real.");
            printHelp();
            System.exit(1);
        }
        List<PatchCommand> patches = new ArrayList<>();
        for (int patientId : patientIds) {
            PatientState ps = book.getPatientState(patientId);
            if (ps.secondShotState == SecondShotState.Ephemeral) {
                SecondShotAppoint appoint = new SecondShotAppoint(ps.secondShotTime);
                PatchState patchState = new PatchState(appoint.encode(), patientId);
                patches.add(patchState);
            } else if (ps.secondShotState == SecondShotState.Appointed) {
                Patient patient = book.getPatient(patientId);
                System.out.printf("(%d) %s has already 2nd shot appointed.\n", patientId, patient.name);
            } else {
                Patient patient = book.getPatient(patientId);
                System.err.printf("Patient (%d) %s has no ephemeral 2nd shot appoint.",
                        patientId, patient.name);
                System.exit(1);
            }
        }
        doApplyPatches(patches);
    }

    private static void injectionDone(String[] args) throws Exception {
        LocalDateTime at = null;
        if (args.length == 2) {
            at = parseAppointTime(args[1]);
        } else {
            System.err.println("Invalid args to injection-done");
            System.exit(1);
        }
        AppointBlock block = book.getAppointBlock(at);
        List<PatchCommand> patches = new ArrayList<>();
        for (AppointSlot slot : block.slots) {
            int patientId = slot.patientId;
            if (slot instanceof FirstShotSlot) {
                patches.add(new PatchState("F", patientId));
            } else if (slot instanceof SecondShotSlot) {
                patches.add(new PatchState("D", patientId));
            } else {
                throw new RuntimeException("Unkonwn slot: " + slot);
            }
        }
        doApplyPatches(patches);
    }

    private static void batchConvertUtoK() throws Exception {
        Map<Integer, RegularPatient> patientMap = executeLogbookAsMap();
        Collection<RegularPatient> patients = patientMap.values();
        List<PatchCommand> patches = patients.stream()
                .filter(p -> p.state instanceof Under65)
                .map(p -> {
                    p = p.copy();
                    p.state = new Kakaritsuke();
                    return new PatchState(p.state.encode(), p.patientId);
                })
                .collect(toList());
        doApplyPatches(patches, patientMap);
    }

    private static void batchRegisterEphemeralSecondAppoint() throws Exception {
        throw new RuntimeException("Not implemented");
//        Context ctx = new Context();
//        AppointCalendarOld cal = ctx.getCalendar();
//        Map<Integer, PatientAppointPref> prefMap = readPatientAppointPrefs();
//        Map<Integer, RegularPatient> patMap = executeLogbookAsMap(ctx.getLogs());
//        List<RegularPatient> patients = patMap.values().stream()
//                .filter(p -> {
//                    return p.state instanceof FirstShotAppoint &&
//                            ((FirstShotAppoint) p.state).tmpSecondAppoint == null;
//                })
//                .collect(toList());
//        Collections.shuffle(patients);
//        List<PatchCommand> patches = new ArrayList<>();
//        for (RegularPatient patient : patients) {
//            FirstShotAppoint state = (FirstShotAppoint) patient.state;
//            PatientAppointPref pref = prefMap.get(patient.patientId);
//            LocalDateTime at = cal.findVacancy(
//                    state.at.toLocalDate().plus(21, ChronoUnit.DAYS),
//                    candidate -> {
//                        if (pref != null) {
//                            return pref.appointPref.acceptable(candidate);
//                        } else {
//                            return true;
//                        }
//                    }
//            );
//            if (at == null) {
//                System.err.println("(No Vacancy: " + patient);
//            } else {
//                state = state.copy();
//                state.tmpSecondAppoint = at;
//                patient = patient.copy();
//                patient.state = state;
//                cal.setEntry(at, AppointFrame.PatientCalendar.TemporarySecondAppoint, patient);
//                patches.add(new PatchState(state.encode(), patient.patientId));
//            }
//        }
//        if (patches.size() > 0) {
//            doApplyPatches(patches, patMap);
//        }
    }

    private static void registerEphemeralSecondAppoint(String[] args) throws Exception {
        throw new RuntimeException("Not implemented.");
//        List<Integer> patientIds = new ArrayList<>();
//        if (args.length > 1) {
//            for (int i = 1; i < args.length; i++) {
//                String arg = args[i];
//                patientIds.add(Integer.parseInt(arg));
//            }
//        } else {
//            System.err.println("Invalid arg to register-ephemeral-second-appoint.");
//            printHelp();
//            System.exit(1);
//        }
//        Collections.shuffle(patientIds);
//        Map<Integer, PatientAppointPref> prefMap = readPatientAppointPrefs();
//        List<String> logs = readLogs();
//        Map<LocalDateTime, AppointFrame> cal = AppointFrame.readFromLogs(logs);
//        Map<Integer, RegularPatient> patMap = executeLogbookAsMap(logs);
//        List<PatchCommand> patches = new ArrayList<>();
//        for (int patientId : patientIds) {
//            RegularPatient patient = patMap.get(patientId);
//            if (patient == null) {
//                System.err.println("Cannot find patient: " + patientId);
//                System.exit(1);
//            }
//            if (patient.state instanceof SecondShotCandidate) {
//                SecondShotCandidate secondShotCandidate = (SecondShotCandidate) patient.state;
//                LocalDate due = secondShotCandidate.firstShotDate.plus(21, ChronoUnit.DAYS);
//                PatientAppointPref pref = prefMap.get(patientId);
//                LocalDateTime at = AppointFrame.findVacancy(due, cal, candidate -> {
//                    if (pref != null) {
//                        return pref.appointPref.acceptable(candidate);
//                    } else {
//                        return true;
//                    }
//                });
//                if (at == null) {
//                    System.err.println("予約枠を見つけられませんでした。" + patient);
//                } else {
//                    System.out.printf("%s -> %s\n", CovidMisc.encodeAppointTime(at), patient);
//                    EphemeralSecondShotAppoint newState = new EphemeralSecondShotAppoint(
//                            at, secondShotCandidate.firstShotDate
//                    );
//                    patches.add(new PatchState(newState.encode(), patient.patientId));
//                }
//            } else {
//                System.err.println("Is not second shot candidate: " + patient);
//                System.exit(1);
//            }
//        }
//        if (patches.size() > 0) {
//            doApplyPatches(patches, patMap);
//        }
    }

    private static void history(String[] args) throws Exception {
        var params = new Object() {
            int patientId;
        };
        if (args.length == 2) {
            params.patientId = Integer.parseInt(args[1]);
        } else {
            System.err.println("Invalid arg to history.");
            printHelp();
            System.exit(1);
        }
        executeLogbook(readLogs(), patient -> {
            if (patient.patientId == params.patientId) {
                System.out.println(patient);
            }
        });
    }

    private static class PrepareCalendarResult {
        AppointCalendar appointCalendar;
        Map<Integer, RegularPatient> patientMap;

        public PrepareCalendarResult(AppointCalendar appointCalendar, Map<Integer, RegularPatient> patientMap) {
            this.appointCalendar = appointCalendar;
            this.patientMap = patientMap;
        }
    }

    private static PrepareCalendarResult prepareCalendar() {
        Map<Integer, PatientState> stateMap = new HashMap<>();
        Map<Integer, RegularPatient> patientMap = new HashMap<>();
        executeLogbook(readLogs(), patient -> {
            PatientState ps = stateMap.computeIfAbsent(patient.patientId, k -> new PatientState());
            ps.apply(patient.state);
            patientMap.put(patient.patientId, patient);
        });
        AppointCalendar cal = new AppointCalendar();
        cal.init();
        for (int patientId : stateMap.keySet()) {
            cal.add(patientId, stateMap.get(patientId));
        }
        return new PrepareCalendarResult(cal, patientMap);
    }

    private static void calendar(String[] args) {
        List<LocalDateTime> targetTimes = null;
        if (args.length == 1) {
            targetTimes = book.listAppointTime();
        } else if (args.length == 2) {
            String arg = args[1];
            Pattern pat = Pattern.compile("(\\d+)-(\\d+)");
            Matcher m = pat.matcher(arg);
            if (m.matches()) {
                int year = LocalDate.now().getYear();
                int month = Integer.parseInt(m.group(1));
                int day = Integer.parseInt(m.group(2));
                LocalDate at = LocalDate.of(year, month, day);
                targetTimes = new ArrayList<>();
                for (LocalDateTime dt : book.listAppointTime()) {
                    if (dt.toLocalDate().equals(at)) {
                        targetTimes.add(dt);
                    }
                }
            }
        }
        if (targetTimes == null) {
            System.err.println("Invalid args to calendar.");
            printHelp();
            System.exit(1);
        }
        for (LocalDateTime at : targetTimes) {
            AppointDate appointDate = book.getAppointDate(at);
            if (appointDate.capacity > 0) {
                System.out.printf("%s (%d)\n", appointTimeRep(at), appointDate.capacity);
                AppointBlock block = book.getAppointBlock(at);
                if (block.slots.size() > 0) {
                    int capacity = book.getAppointDate(at).capacity;
                    int index = 1;
                    for (AppointSlot slot : block.slots) {
                        Patient patient = book.getPatient(slot.patientId);
                        System.out.printf("%d. (%d) %s %s\n", index++, patient.patientId, patient.name,
                                slot.renderState());
                    }
                    for (; index <= capacity; index++) {
                        System.out.printf("%d.\n", index);
                    }
                }
                System.out.println();
            }
        }
    }

    private enum ThirdShotCandidate {
        Medical, Elder, Other
    }

    private static LocalDate thirdShotDue(LocalDate secondShotDate, ThirdShotCandidate candidate) {
        switch (candidate) {
            case Medical: {
                return secondShotDate.plusMonths(6);
            }
            case Elder: {
                LocalDate d = secondShotDate.plusMonths(7);
                if (d.getMonthValue() <= 2) {
                    return d;
                }
                d = secondShotDate.plusMonths(6);
                LocalDate march = LocalDate.of(2022, 3, 1);
                if( d.isBefore(march) ){
                    return march;
                } else {
                    return d;
                }
            }
            case Other: {
                LocalDate d = secondShotDate.plusMonths(8);
                if (d.getMonthValue() <= 2) {
                    return d;
                }
                d = secondShotDate.plusMonths(7);
                LocalDate march = LocalDate.of(2022, 3, 1);
                if( d.isBefore(march) ){
                    return march;
                } else {
                    return d;
                }
            }
            default: {
                throw new RuntimeException("cannot happen");
            }
        }
    }

    private static class SecondShotData {
        public LocalDate at;
        public SecondShotSlot slot;

        public SecondShotData(LocalDate at, SecondShotSlot slot) {
            this.at = at;
            this.slot = slot;
        }
    }

    private static void calendar2nd(String[] args) {
        List<LocalDateTime> targetTimes = book.listAppointTime();
        Map<LocalDate, List<SecondShotData>> map = new HashMap<>();
        for (LocalDateTime at : targetTimes) {
            AppointBlock block = book.getAppointBlock(at);
            if (block.slots.size() > 0) {
                for (AppointSlot slot : block.slots) {
                    if (slot instanceof SecondShotSlot) {
                        SecondShotSlot slot2 = (SecondShotSlot) slot;
                        if (slot2.state == SecondShotState.Done) {
                            Patient patient = book.getPatient(slot2.patientId);
                            ThirdShotCandidate candidate = ThirdShotCandidate.Other;
                            if (patient.patientId == 3835) {
                                candidate = ThirdShotCandidate.Medical;
                            } else if (patient.age >= 65) {
                                candidate = ThirdShotCandidate.Elder;
                            }
                            LocalDate dueDate = thirdShotDue(at.toLocalDate(), candidate);
                            SecondShotData data = new SecondShotData(at.toLocalDate(), slot2);
                            if (map.containsKey(dueDate)) {
                                map.get(dueDate).add(data);
                            } else {
                                List<SecondShotData> items = new ArrayList<>();
                                items.add(data);
                                map.put(dueDate, items);
                            }
                        }
                    }
                }
            }
        }
        List<LocalDate> dueDates = new ArrayList<>(map.keySet());
        dueDates.sort(Comparator.naturalOrder());
        dueDates.forEach(due -> {
            System.out.printf("%s\n", due);
            List<SecondShotData> items = map.get(due);
            items.forEach(item -> {
                Patient patient = book.getPatient(item.slot.patientId);
                System.out.printf("  (%04d) %s %d才 %s\n", patient.patientId, patient.name,
                        patient.age, item.at);
            });
        });
        int totalCount = 0;
        for (List<SecondShotData> items : map.values()) {
            totalCount += items.size();
        }
        System.out.printf("Total: %d名\n", totalCount);
    }

    private static void calendar2ndList(String[] args) {
        List<LocalDateTime> targetTimes = book.listAppointTime();
        for (LocalDateTime at : targetTimes) {
            AppointBlock block = book.getAppointBlock(at);
            for (AppointSlot slot : block.slots) {
                if (slot instanceof SecondShotSlot) {
                    SecondShotSlot slot2 = (SecondShotSlot) slot;
                    if (slot2.state == SecondShotState.Done) {
                        Patient patient = book.getPatient(slot2.patientId);
                        ThirdShotCandidate candidate = ThirdShotCandidate.Other;
                        if (patient.patientId == 3835) {
                            candidate = ThirdShotCandidate.Medical;
                        } else if (patient.age >= 65) {
                            candidate = ThirdShotCandidate.Elder;
                        }
                        LocalDate dueDate = thirdShotDue(at.toLocalDate(), candidate);
                        System.out.printf("%d %d %s %s\n", patient.patientId, patient.age,
                                at.toLocalDate(), dueDate);
                    }
                }
            }
        }
    }

    private static void pickRandom(String[] args) {
        int minValue = 0;
        int maxValue = 0;
        if (args.length == 3) {
            minValue = Integer.parseInt(args[1]);
            maxValue = Integer.parseInt(args[2]);
        } else {
            System.err.println("Invalid arg to random.");
            printHelp();
            System.exit(1);
        }
        int r = Misc.randomValue(minValue, maxValue);
        System.out.printf("%d\n", r);
    }

    private static void batchUpdatePhone() throws Exception {
        Client client = Misc.getClient();
        List<PatchCommand> patches = new ArrayList<>();
        for (Patient patient : book.listPatient()) {
            PatientDTO dto = client.getPatient(patient.patientId);
            if (patient.phone == null || !patient.phone.equals(dto.phone)) {
                patches.add(new PatchPhone(patient.patientId, dto.phone));
            }
        }
        doApplyPatches(patches);
    }

    private static List<PatchCommand> composeAddPatientPatches(Client client, int patientId) {
        List<PatchCommand> patches = new ArrayList<>();
        PatientDTO patient = client.getPatient(patientId);
        RegularPatient regp = CovidMisc.patientDtoToRegularPatient(patient);
        patches.add(new PatchAdd(regp));
        patches.add(new PatchState(regp.state.encode(), patient.patientId));
        return patches;
    }

    private static void addPatient(String[] args) throws Exception {
        var params = new Object() {
            int patientId;
        };
        if (args.length == 2) {
            params.patientId = Integer.parseInt(args[1]);
        } else {
            System.err.println("Invalid arg to add-patient");
            printHelp();
            System.exit(1);
        }
        Patient patient = book.getPatient(params.patientId);
        if (patient != null) {
            System.err.printf("Patient (%d) %s is already added.", patient.patientId, patient.name);
            System.exit(1);
        }
        Client client = Misc.getClient();
        List<PatchCommand> patches = composeAddPatientPatches(client, params.patientId);
        doApplyPatches(patches);
    }

    private static void appointsAt(String[] args) {
        throw new RuntimeException("Not implemented");
//        var params = new Object() {
//            final List<LocalDateTime> atTimes = new ArrayList<>();
//        };
//        if (args.length >= 2) {
//            for (int i = 1; i < args.length; i++) {
//                params.atTimes.add(parseAppointTime(args[i]));
//            }
//        } else {
//            System.err.println("Invalid arg to appoints-at");
//            System.err.println("example -- appoints-at 06-19T14:00");
//            System.exit(1);
//        }
//        Context ctx = new Context();
//        AppointCalendarOld cal = ctx.getCalendar();
//        for (LocalDateTime at : params.atTimes) {
//            System.out.printf("%s %02d時%02d分\n",
//                    Misc.localDateToKanji(at.toLocalDate(), true, true),
//                    at.getHour(), at.getMinute());
//            AppointFrame frame = cal.getFrame(at);
//            List<RegularPatient> patients = frame.getEntries().stream().map(e -> e.patient).collect(toList());
//            int index = 1;
//            for (RegularPatient p : patients) {
//                System.out.printf("%d. %s\n", index++, p);
//            }
//            System.out.println();
//
//        }
    }

    static List<RegularPatient> getAppointsAt(LocalDateTime at) {
        throw new RuntimeException("Not implemented");
//        AppointCalendarOld cal = ctx.getCalendar();
//        return cal.getFrame(at).getEntries().stream()
//                .map(e -> e.patient).collect(toList());
//        Map<LocalDateTime, AppointFrame> cal = AppointFrame.readFromLogs(readLogs());
//        AppointFrame frame = cal.get(at);
//        if (frame == null) {
//            return Collections.emptyList();
//        } else {
//            return frame.getEntries().stream().map(e -> e.patient).collect(toList());
//        }
    }

    private static void registerAppoint(String[] args) throws Exception {
        var params = new Object() {
            LocalDateTime at;
            final List<Integer> patientIds = new ArrayList<>();
        };
        if (args.length > 1) {
            params.at = parseAppointTime(args[1]);
            for (int i = 2; i < args.length; i++) {
                params.patientIds.add(Integer.parseInt(args[i]));
            }
        } else {
            System.err.println("Invalid arg to register-appoint.");
            System.exit(1);
        }
        AppointBlock block = book.getAppointBlock(params.at);
        if (block == null) {
            System.err.printf("Invalid appoint date: %s\n", params.at);
            System.exit(1);
        }
        int avail = block.getCapacity() - block.slots.size();
        if (avail < params.patientIds.size()) {
            System.err.println("Not enough available slots.");
            System.exit(1);
        }
        LocalDateTime at = params.at;
        List<PatchCommand> patches = new ArrayList<>();
        Client client = null;
        for (int patientId : params.patientIds) {
            PatientState ps = book.getPatientState(patientId);
            if (ps == null) {
                if (client == null) {
                    client = Misc.getClient();
                }
                List<PatchCommand> localPatches = composeAddPatientPatches(client, patientId);
                patches.addAll(localPatches);
                book.applyPatches(localPatches);
                ps = book.getPatientState(patientId);
            }
            if (ps.firstShotTime == null && ps.secondShotTime == null) {
                FirstShotAppoint e = new FirstShotAppoint(at);
                PatchState patch = new PatchState(e.encode(), patientId);
                patches.add(patch);
                LocalDate due = at.toLocalDate().plus(21, ChronoUnit.DAYS);
                AppointPref pref = book.getAppointPref(patientId);
                LocalDateTime second = book.findVacancy(due, pref);
                if (second == null) {
                    Patient patient = book.getPatient(patientId);
                    System.err.printf("Cannot find 2nd shot appoint for (%d) %s\n", patientId, patient.name);
                    System.exit(1);
                }
                EphemeralSecondShotAppoint secondEvt = new EphemeralSecondShotAppoint(second);
                PatchState secondPatch = new PatchState(secondEvt.encode(), patientId);
                patches.add(secondPatch);
                StateLog stateLog = new StateLog(patientId, secondEvt);
                book.addStateLog(stateLog);
            } else {
                System.err.printf("Not eligible for first shot appoint: (%d) %s\n",
                        patientId,
                        book.getPatient(patientId).name);
            }
        }
        doApplyPatches(patches);
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

    static List<AppointDate> readAppointDates() {
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
        PatientEvent state = p.state;
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
        PrepareCalendarResult prepResult = prepareCalendar();
        AppointCalendar cal = prepResult.appointCalendar;
        System.out.printf("%s (%d)\n\n", appointTimeRep(params.at),
                cal.getAppointDate(params.at).capacity);
        var ctx = new Object() {
            int index = 1;
        };
        cal.iterItem(params.at, (patientId, firstShotState) -> {
            RegularPatient p = prepResult.patientMap.get(patientId);
            System.out.printf("%d. (%d) %s %s\n", ctx.index++, p.patientId, p.name,
                    firstShotState.toString());
        }, (patientId, secondShotState) -> {
            RegularPatient p = prepResult.patientMap.get(patientId);
            System.out.printf("%d. (%d) %s %s\n", ctx.index++, p.patientId, p.name,
                    secondShotState.toString());
        });
        for (int i = ctx.index; i <= cal.getAppointDate(params.at).capacity; i++) {
            System.out.printf("%d.\n", i);
        }
        System.out.println();
        List<RegularPatient> candidates = executeLogbook().stream()
                .filter(p -> p.state instanceof FirstShotCandidate || p.state instanceof Kakaritsuke)
                .filter(p -> {
                    AppointPref pref = book.getAppointPref(p.patientId);
                    return pref == null || pref.acceptable(params.at);
                })
                .collect(toList());
        Collections.shuffle(candidates);
        candidates.forEach(System.out::println);
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

    private static boolean doApplyPatches(List<PatchCommand> patches) throws Exception {
        if (patches.size() == 0) {
            System.out.println("Nothing to apply.");
            return false;
        }
        for (PatchCommand patch : patches) {
            if (patch instanceof PatchAdd) {
                PatchAdd patchAdd = (PatchAdd) patch;
                System.out.printf("ADD %s\n", patchAdd.patient);
            } else if (patch instanceof PatchState) {
                PatchState patchState = (PatchState) patch;
                Patient patient = book.getPatient(patchState.patientId);
                System.out.printf("STATE %s %s\n", patchState.attr, patient);
            } else if (patch instanceof PatchPhone) {
                PatchPhone patchPhone = (PatchPhone) patch;
                Patient patient = book.getPatient(patchPhone.patientId);
                patient.phone = patchPhone.phone;
                System.out.printf("PHONE %s\n", patient);
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

    private static boolean doApplyPatches(List<PatchCommand> patches, Map<Integer, RegularPatient> patientMap)
            throws Exception {
        throw new RuntimeException("Not implemented");
//        for (PatchCommand patch : patches) {
//            if (patch instanceof PatchAdd) {
//                PatchAdd patchAdd = (PatchAdd) patch;
//                System.out.printf("ADD %s\n", patchAdd.patient);
//            } else if (patch instanceof PatchState) {
//                PatchState patchState = (PatchState) patch;
//                RegularPatient patient = patientMap.get(patchState.patientId).copy();
//                patient.state = CovidMisc.parsePatientAttr(patchState.attr);
//                System.out.printf("STATE %s\n", patient);
//            } else if (patch instanceof PatchPhone) {
//                PatchPhone patchPhone = (PatchPhone) patch;
//                RegularPatient patient = patientMap.get(patchPhone.patientId).copy();
//                patient.phone = patchPhone.phone;
//                System.out.printf("PHONE %s\n", patient);
//            } else {
//                throw new RuntimeException("Unknown patch: " + patch);
//            }
//        }
//        System.console().writer().print("Apply these patches? (Y/N) ");
//        System.console().writer().flush();
//        String input = System.console().readLine();
//        if (input.startsWith("Y")) {
//            List<String> patchLines = patches.stream().map(PatchCommand::encode).collect(toList());
//            Misc.appendLines(logbookPath.toString(), patchLines);
//            return true;
//        } else {
//            return false;
//        }
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
                rp.state = CovidMisc.parsePatientAttr(ps.attr);
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
        CovidMisc.parsePatientAttr(attr);
        String patch = logbookState(patientId, attr);
        Misc.appendLines(patchPath.toString(), Collections.singletonList(patch));
    }

    private static void patch(String[] args) throws Exception {
        if (args.length >= 3) {
            String attr = args[1];
            CovidMisc.parsePatientAttr(attr);
            List<PatchCommand> patches = new ArrayList<>();
            Map<Integer, RegularPatient> patientMap = executeLogbookAsMap();
            for (int i = 2; i < args.length; i++) {
                int patientId = Integer.parseInt(args[i]);
                if (patientMap.get(patientId) == null) {
                    throw new RuntimeException("Unknown patient: " + patientId);
                }
                patches.add(new PatchState(attr, patientId));
            }
            if (patches.size() > 0) {
                doApplyPatches(patches);
            }
        } else {
            System.err.println("Invalid argss to patch.");
            printHelp();
            System.exit(1);
        }
    }

    private static void dueSecondShot() throws Exception {
        List<RegularPatient> patients = executeLogbook();
        Map<LocalDate, List<RegularPatient>> map = new HashMap<>();
        for (RegularPatient patient : patients) {
            PatientEvent st = patient.state;
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
                if (p.state instanceof NeedConfirm && p.age < 65) {
                    p.state = new Under65();
                }
                logs.add(logbookState(p.patientId, p.state.encode()));
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
            String server = System.getenv("MYCLINIC_SERVICE");
            if (server == null) {
                throw new RuntimeException("Cannot find env var: MYCLINIC_SERVICE");
            }
            Client client = new Client(server);
            PatientDTO patient = client.getPatient(patientId);
            return String.format("%s %s", patient.lastNameYomi, patient.firstNameYomi);
        }
    }

    interface PatchCommand {
        String encode();
    }

    static class PatchAdd implements PatchCommand {
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

    static class PatchState implements PatchCommand {
        public String attr;
        public int patientId;

        public PatchState(String attr, int patientId) {
            this.attr = attr;
            this.patientId = patientId;
            CovidMisc.parsePatientAttr(attr);
        }

        @Override
        public String encode() {
            return String.format("STATE %d %s", patientId, attr);
        }
    }

    static class PatchPhone implements PatchCommand {
        public int patientId;
        public String phone;

        public PatchPhone(int patientId, String phone) {
            this.patientId = patientId;
            this.phone = phone;
        }

        @Override
        public String encode() {
            return String.format("PHONE %d %s", patientId, phone);
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
                p.state = p.age >= 65 ? new NeedConfirm() : new Under65();
                return new PatchAdd(p);
            }
            case "STATE": {
                String[] items = parts[1].split("\\s+", 2);
                if (items.length != 2) {
                    throw new RuntimeException("Invalid log: " + patch);
                }
                int patientId = Integer.parseInt(items[0]);
                String attr = items[1];
                CovidMisc.parsePatientAttr(attr);
                return new PatchState(attr, patientId);
            }
            case "PHONE": {
                String[] items = parts[1].split("\\s+", 2);
                if (items.length != 2) {
                    throw new RuntimeException("Invalid log: " + patch);
                }
                int patientId = Integer.parseInt(items[0]);
                String phone = items[1];
                return new PatchPhone(patientId, phone);
            }
            default: {
                throw new RuntimeException("Invalid command: " + patch);
            }
        }
    }

    static List<String> readLogs() {
        return Misc.readLines(logbookPath.toString());
    }

    private static List<RegularPatient> executeLogbook() {
        return executeLogbook(readLogs());
    }

    private static List<RegularPatient> executeLogbook(List<String> logs) {
        return executeLogbook(logs, p -> {
        });
    }

    private static List<RegularPatient> executeLogbook(List<String> logs, Consumer<RegularPatient> callback) {
        return executeLogbook(logs, (p, b) -> callback.accept(p));
    }

    public static List<RegularPatient> executeLogbook(List<String> logs,
                                                      BiConsumer<RegularPatient, Boolean> callback) {
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
                callback.accept(p, true);
            } else if (cmd instanceof PatchState) {
                PatchState ps = (PatchState) cmd;
                int patientId = ps.patientId;
                String attr = ps.attr;
                PatientEvent state = CovidMisc.parsePatientAttr(attr);
                RegularPatient patient = map.get(patientId);
                if (patient == null) {
                    throw new RuntimeException("Invalid patient-id: " + String.format("%d", patientId));
                }
                patient.state = state;
                callback.accept(patient, true);
            } else if (cmd instanceof PatchPhone) {
                PatchPhone pp = (PatchPhone) cmd;
                RegularPatient patient = map.get(pp.patientId);
                if (patient == null) {
                    throw new RuntimeException("Invalid patient-id: " + String.format("%d", pp.patientId));
                }
                patient.phone = pp.phone;
                callback.accept(patient, false);
            } else {
                throw new RuntimeException("Unknown patch: " + log);
            }
        }
        return new ArrayList<>(map.values());
    }

    static Map<Integer, RegularPatient> executeLogbookAsMap(List<String> logs) {
        Map<Integer, RegularPatient> map = new HashMap<>();
        executeLogbook(logs).forEach(p -> {
            if (map.containsKey(p.patientId)) {
                throw new RuntimeException("Duplicate patient-ids in logbook.");
            }
            map.put(p.patientId, p);
        });
        return map;
    }

    static Map<Integer, RegularPatient> executeLogbookAsMap() {
        Map<Integer, RegularPatient> map = new HashMap<>();
        executeLogbook().forEach(p -> {
            if (map.containsKey(p.patientId)) {
                throw new RuntimeException("Duplicate patient-ids in logbook.");
            }
            map.put(p.patientId, p);
        });
        return map;
    }

    private static Map<String, List<RegularPatient>> prepareGroupsForListing() {
        return prepareGroupsForListing(null);
    }

    private static Map<String, List<RegularPatient>> prepareGroupsForListing(List<String> select) {
        List<RegularPatient> patients = executeLogbook();
        Map<String, List<RegularPatient>> groups = new HashMap<>();
        patients.forEach(p -> {
            String code = p.state.encode().substring(0, 1);
            if (select != null && !select.contains(code)) {
                return;
            }
            List<RegularPatient> list = groups.computeIfAbsent(code, k -> new ArrayList<>());
            list.add(p);
        });
        if (select != null) {
            return groups;
        } else {
            List<String> codes = new ArrayList<>();
            Set<String> keys = new HashSet<>(groups.keySet());
            List.of("C", "S", "*", "P", "T", "U").forEach(c -> {
                codes.add(c);
                keys.remove(c);
            });
            keys.remove("x");
            //keys.remove("U");
            codes.addAll(keys);
            Map<String, List<RegularPatient>> result = new LinkedHashMap<>();
            for (String code : codes) {
                result.put(code, groups.get(code));
            }
            return result;
        }
    }

    private static List<String> prepareList() throws Exception {
        return prepareList(null);
    }

    private static List<String> prepareList(List<String> select) throws Exception {
        Map<String, List<RegularPatient>> groups = prepareGroupsForListing(select);
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

    private static void list(String[] args) throws Exception {
        List<String> select = null;
        if (args.length > 1) {
            select = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                select.add(args[i]);
            }
        }
        List<String> lines = prepareList(select);
        lines.forEach(System.out::println);
    }

    private static void current() throws Exception {
        List<String> lines = prepareList();
        Path currentPath = Path.of(CovidVaccineDir, "current.txt");
        Misc.saveLines(currentPath.toString(), lines);
    }

    private static void patientIds(String[] args) throws Exception {
        String target = null;
        if (args.length == 2) {
            target = args[1];
        } else {
            System.err.println("Invalid arg to patient-ids");
            System.exit(1);
        }
        Set<Integer> set = new HashSet<>();
        Map<Integer, RegularPatient> patMap = new HashMap<>();
        executeLogbook(readLogs(), patient -> {
            PatientEvent e = patient.state;
            if (e instanceof Kakaritsuke) {
                set.add(patient.patientId);
            } else if (e instanceof Under65) {
                if (set.contains(patient.patientId)) {
                    set.remove(patient.patientId);
                }
            }
            patMap.put(patient.patientId, patient);
        });
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i));
            if (i != list.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
        list.forEach(patientId -> {
            System.out.printf("%d %s\n", patientId, patMap.get(patientId).name);
        });
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

    static LocalDateTime tryParseAppointTime(String src) {
        try {
            return parseAppointTime(src);
        } catch (Exception e) {
            return null;
        }
    }

    static String appointTimeRep(LocalDateTime at) {
        return String.format("%s %02d時%02d分",
                Misc.localDateToKanji(at.toLocalDate(), true, true),
                at.getHour(), at.getMinute());
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
            rp.state = CovidMisc.parsePatientAttr(parts[0]);
            rp.patientId = Integer.parseInt(parts[1]);
            rp.name = parts[2];
            rp.age = Integer.parseInt(parts[3]);
            rp.phone = parts[4];
            return rp;
        }
    }

}

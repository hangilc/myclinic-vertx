package dev.myclinic.vertx.cli;

import java.nio.file.Path;
import java.util.List;

public class CovidVaccine {

    private static String CovidVaccineDir = System.getenv("COVID_VACCINE_DIR");

    public static void main(String[] args) throws Exception {
        String stamp = Misc.makeTimestamp();
        System.out.println(stamp);
        readRegularPatients();
    }

    private static class RegularPatient {
        int patientId;
        String name;
        String phone;
    }

    private static List<RegularPatient> readRegularPatients(){
        Path file = Path.of(CovidVaccineDir, "regular-patients.txt");
        List<String> lines = Misc.readLines(file.toString());
        lines.forEach(System.out::println);
        return null;
    }

}

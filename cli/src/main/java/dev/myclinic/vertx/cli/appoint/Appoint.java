package dev.myclinic.vertx.cli.appoint;

import dev.myclinic.vertx.appoint.AppointAPI;
import dev.myclinic.vertx.appoint.AppointDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Appoint {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            System.exit(0);
        }
        switch (args[0]) {
            case "add-appoint-time": {
                addAppointTime(args);
                break;
            }
            case "list-appoint-time": {
                listAppointTime(args);
                break;
            }
            case "put-appoint": {
                putAppoint(args);
                break;
            }
            default: {
                System.err.printf("Unknown command %s.\n", args[0]);
                printHelp();
                System.exit(1);
            }
        }
    }

    private static void printHelp() {
        System.err.println("Appoint COMMAND [ARGS..]");
        System.err.println("  add-appoint-time DATE TIME...");
        System.err.println("  list-appoint-time");
        System.err.println("  put-appoint-time DATE TIME NAME [PATIENT-ID]");
    }

    private static void putAppoint(String[] args) throws Exception {
        var params = new Object() {
            LocalDate date;
            LocalTime time;
            String name;
            Integer patientId;
        };
        if (args.length == 4 || args.length == 5) {
            params.date = AppointMisc.readAppointDate(args[1]);
            params.time = AppointMisc.readAppointTime(args[2]);
            params.name = args[3];
            if (args.length >= 5) {
                params.patientId = Integer.parseInt(args[4]);
            }
        } else {
            System.err.println("Invalid args to put-appoint");
            printHelp();
            System.exit(1);
        }
        AppointMisc.withConnection(conn -> {
            AppointAPI.putAppoint(conn, params.date, params.time, params.name);
        });
    }

    private static void listAppointTime(String[] args) {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate upto = LocalDate.of(2099, 12, 31);
        AppointMisc.withConnection(conn -> {
            List<AppointDTO> apps = AppointAPI.listAppointTime(conn, from, upto);
            for (AppointDTO app : apps) {
                System.out.println(app);
            }

        });
    }

    private static void addAppointTime(String[] args) {
        var params = new Object() {
            LocalDate date;
        };
        List<LocalTime> times = new ArrayList<>();
        if (args.length >= 3) {
            params.date = AppointMisc.readAppointDate(args[1]);
            for (int i = 2; i < args.length; i++) {
                times.add(AppointMisc.readAppointTime(args[i]));
            }
        } else {
            System.err.println("Invalid args to add-appoint-time.");
            printHelp();
            System.exit(1);
        }
        AppointMisc.withConnection(conn -> {
            for (LocalTime t : times) {
                AppointAPI.createAppointTime(conn, params.date, t);
            }
        });
    }

}

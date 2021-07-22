package dev.myclinic.vertx.cli.appoint;

import dev.myclinic.vertx.appoint.AppointAPI;
import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.cli.Misc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
            case "cancel-appoint": {
                cancelAppoint(args);
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
        System.err.println("  put-appoint DATE TIME NAME [PATIENT-ID]");
        System.err.println("  cancel-appoint DATE TIME");
    }

    private static void cancelAppoint(String[] args) throws Exception {
        var params = new Object(){
            LocalDate date;
            LocalTime time;
        };
        if( args.length == 3 ){
            params.date = AppointMisc.readAppointDate(args[1]);
            params.time = AppointMisc.readAppointTime(args[2]);
        } else {
            System.err.println("Invalid args to cancel-appoint");
            printHelp();
            System.exit(1);
        }
        AppointMisc.withConnection(conn -> {
            AppointDTO app = AppointAPI.getAppoint(conn, params.date, params.time);
            if( app == null || app.patientName == null ){
                System.out.println("Cannot find appoint.");
                System.exit(1);
            } else {
                confirmProceed(() -> {
                    System.out.println("予約のキャンセル");
                    System.out.println(app.date);
                    System.out.println(app.time);
                    System.out.println(app.patientName);
                    if( app.patientId > 0 ){
                        System.out.printf("patient-id: %d\n", app.patientId);
                    }
                });
                AppointAPI.cancelAppoint(conn, params.date, params.time);
            }
        });
    }

    private static void putAppoint(String[] args) throws Exception {
        var params = new Object() {
            LocalDate date;
            LocalTime time;
            String name;
            int patientId;
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
        confirmProceed(() -> {
            System.out.println(params.date);
            System.out.println(params.time);
            System.out.println(params.name);
            if( params.patientId > 0 ) {
                System.out.println(params.patientId);
            }
        });
        AppointMisc.withConnection(conn -> {
            AppointDTO dto = new AppointDTO(params.date, params.time);
            dto.patientName = params.name;
            dto.patientId = params.patientId;
            AppointAPI.putAppoint(conn, dto);
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
        confirmProceed(() -> {
            System.out.println(params.date);
            times.forEach(System.out::println);
        });
        AppointMisc.withConnection(conn -> {
            for (LocalTime t : times) {
                AppointAPI.createAppointTime(conn, params.date, t);
            }
        });
    }

    private static void confirmProceed(Runnable prompt){
        prompt.run();
        System.console().writer().print("Proceed? (Y/N) ");
        System.console().writer().flush();
        String input = System.console().readLine();
        if (!input.startsWith("Y")) {
            System.exit(1);
        }
    }

}

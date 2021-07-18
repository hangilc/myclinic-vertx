package dev.myclinic.vertx.cli.appoint;

import dev.myclinic.vertx.appoint.AppointDTO;
import dev.myclinic.vertx.appoint.AppointPersist;
import dev.myclinic.vertx.appoint.Misc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static dev.myclinic.vertx.appoint.Misc.fromSqlDate;
import static dev.myclinic.vertx.appoint.Misc.fromSqlTime;

public class Appoint {

    public static void main(String[] args) throws Exception {
        if( args.length == 0 ){
            printHelp();
            System.exit(0);
        }
        switch(args[0]){
            case "add-appoint-time": {
                addAppointTime(args);
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

    private static void printHelp(){
        System.err.println("Appoint COMMAND [ARGS..]");
        System.err.println("  add-appoint-time DATE TIME...");
        System.err.println("  put-appoint-time DATE TIME NAME [PATIENT-ID]");
    }

    private static void putAppoint(String[] args) throws Exception {
        LocalDate date = null;
        LocalTime time = null;
        String name = null;
        Integer patientId = null;
        if( args.length == 4 || args.length == 5 ){
            date = Misc.readAppointDate(args[1]);
            time = Misc.readAppointTime(args[2]);
            name = args[3];
            if( args.length >= 5 ){
                patientId = Integer.parseInt(args[4]);
            }
        } else {
            System.err.println("Invalid args to put-appoint");
            printHelp();
            System.exit(1);
        }
        AppointDTO app = new AppointDTO(date, time);
        app.patientName = name;
        app.patientId = patientId;
        app.appointedAt = LocalDateTime.now();
        AppointMisc.withConnection(conn -> {
            AppointPersist.enterAppoint(conn, app);
        });
    }

    private static void addAppointTime(String[] args) {
        var params = new Object(){
            LocalDate date;
        };
        List<LocalTime> times = new ArrayList<>();
        if( args.length >= 3 ){
            params.date = fromSqlDate(args[1]);
            for(int i=2;i<args.length;i++){
                times.add(fromSqlTime(args[i]));
            }
        } else {
            System.err.println("Invalid args to add-appoint-time.");
            printHelp();
            System.exit(1);
        }
        AppointMisc.withConnection(conn -> {
            try {
                for (LocalTime t : times) {
                    AppointDTO app = new AppointDTO(params.date, t);
                    AppointPersist.enterAppoint(conn, app);
                }
            } catch(Exception ex){
                throw new RuntimeException(ex);
            }
        });
    }

}

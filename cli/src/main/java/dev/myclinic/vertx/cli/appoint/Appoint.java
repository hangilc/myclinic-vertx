package dev.myclinic.vertx.cli.appoint;

import dev.myclinic.vertx.appoint.AppointPersist;
import dev.myclinic.vertx.cli.Misc;

import java.time.LocalDate;
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
    }

    private static void addAppointTime(String[] args) throws Exception {
        LocalDate date = null;
        List<LocalTime> times = new ArrayList<>();
        if( args.length >= 3 ){
            date = fromSqlDate(args[1]);
            for(int i=2;i<args.length;i++){
                times.add(fromSqlTime(args[i]));
            }
        } else {
            System.err.println("Invalid args to add-appoint-time.");
            printHelp();
            System.exit(1);
        }
        times.forEach(t -> {
            AppointPersist.enterAppoint(date, t);
        });
    }

}

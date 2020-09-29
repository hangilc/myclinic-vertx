package dev.myclinic.vertx.rcpt.check;

import java.util.List;
import java.util.function.Consumer;

public class RunEnv {

    public int year;
    public int month;
    public Fixer api;
    public List<Integer> patientIds;
    public boolean fixit;
    public Consumer<Error> errorHandler;
    public boolean verbose;
    public boolean debugHttp;

}

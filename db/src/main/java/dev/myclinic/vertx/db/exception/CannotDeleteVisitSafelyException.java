package dev.myclinic.vertx.db.exception;

public class CannotDeleteVisitSafelyException extends RuntimeException {

    public CannotDeleteVisitSafelyException(String msg) {
        super(msg);
    }

}

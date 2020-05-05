package dev.myclinic.vertx.db.exception;

import java.sql.SQLIntegrityConstraintViolationException;

public class IntegrityException extends RuntimeException {

    public IntegrityException(SQLIntegrityConstraintViolationException e) {
        super(e);
    }

}

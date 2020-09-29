package dev.myclinic.vertx.rcpt.unit;

interface Extendable<T> {

    boolean isExtendableWith(T a);
    void extendWith(T a);

}

package dev.myclinic.vertx.rcpt.unit;

public interface Mergeable<T> {

    boolean isMergeableWith(T arg);
    int getCount();
    void incCount(int n);

}

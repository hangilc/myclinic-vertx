package dev.myclinic.vertx.rcpt.create.bill;

import dev.myclinic.vertx.rcpt.create.output.Output;

@FunctionalInterface
public interface TekiyouProc {
    TekiyouProc noOutput = (output, shuukei, tanka, count) -> {};
    void outputTekiyou(Output output, String shuukei, int tanka, int count);
}

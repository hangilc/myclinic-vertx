package dev.myclinic.vertx.dto.houkatsukensa;

public class HoukatsuKensaStep {

    public int threshold;
    public int point;

    @Override
    public String toString() {
        return "HoukatsuKensaStep{" +
                "threshold=" + threshold +
                ", point=" + point +
                '}';
    }

}

package dev.myclinic.vertx.util.kanjidate;

public class GengouNenPair {

    public Gengou gengou;
    public int nen;

    public GengouNenPair(Gengou gengou, int nen) {
        this.gengou = gengou;
        this.nen = nen;
    }

    @Override
    public String toString() {
        return "GengouNenPair{" +
                "gengou=" + gengou +
                ", nen=" + nen +
                '}';
    }
}

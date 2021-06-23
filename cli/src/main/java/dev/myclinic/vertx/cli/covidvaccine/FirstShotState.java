package dev.myclinic.vertx.cli.covidvaccine;

public enum FirstShotState {

    None,
    Appointed,
    External,
    Done;

    @Override
    public String toString(){
        switch(this){
            case None: return "(None)";
            case Appointed: return "１回目予約";
            case External: return "１回目外部接種";
            case Done: return "１回目接種済";
            default: throw new RuntimeException("Unknown FirstShotState: " + this);
        }
    }


}

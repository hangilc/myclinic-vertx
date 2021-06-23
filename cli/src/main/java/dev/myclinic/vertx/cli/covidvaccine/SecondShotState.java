package dev.myclinic.vertx.cli.covidvaccine;

public enum SecondShotState {

    None,
    Ephemeral,
    Appointed,
    External,
    Done;

    @Override
    public String toString(){
        switch(this){
            case None: return "(None)";
            case Ephemeral: return "２回目暫定予約";
            case Appointed: return "２回目予約";
            case External: return "２回目外部接種";
            case Done: return "２回目接種済";
            default: throw new RuntimeException("Unknown SecondShotState: " + this);
        }
    }

}

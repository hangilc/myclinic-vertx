package dev.myclinic.vertx.consts;

/**
 * Created by hangil on 2017/06/04.
 */
public enum PharmaQueueState {
    WaitPack(dev.myclinic.vertx.consts.MyclinicConsts.PharmaQueueStateWaitPack),
    InPack(dev.myclinic.vertx.consts.MyclinicConsts.PharmaQueueStateInPack),
    PackDone(dev.myclinic.vertx.consts.MyclinicConsts.PharmaQueueStatePackDone);

    private int code;

    PharmaQueueState(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static dev.myclinic.vertx.consts.PharmaQueueState fromCode(int code){
        for(dev.myclinic.vertx.consts.PharmaQueueState state: values()){
            if( state.code == code ){
                return state;
            }
        }
        return null;
    }
}

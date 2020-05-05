package dev.myclinic.vertx.consts;

public enum WqueueWaitState
{
	WaitExam(dev.myclinic.vertx.consts.MyclinicConsts.WqueueStateWaitExam, "診待"),
	InExam(dev.myclinic.vertx.consts.MyclinicConsts.WqueueStateInExam, "診中"),
	WaitCashier(dev.myclinic.vertx.consts.MyclinicConsts.WqueueStateWaitCashier, "会待"),
	WaitDrug(dev.myclinic.vertx.consts.MyclinicConsts.WqueueStateWaitDrug, "薬待"),
	WaitReExam(dev.myclinic.vertx.consts.MyclinicConsts.WqueueStateWaitReExam, "再待");

	private final int code;
	private final String label;

	WqueueWaitState(int code, String label){
		this.code = code;
		this.label = label;
	}

	public int getCode(){
		return code;
	}

	public String getLabel(){
		return label;
	}

	public static dev.myclinic.vertx.consts.WqueueWaitState fromCode(int code){
		for(dev.myclinic.vertx.consts.WqueueWaitState state: dev.myclinic.vertx.consts.WqueueWaitState.values()){
			if( state.code == code ){
				return state;
			}
		}
		return null;
	}

	public static String codeToLabel(int code){
		dev.myclinic.vertx.consts.WqueueWaitState state = fromCode(code);
		if( state == null ){
			return "不明";
		} else {
			return state.getLabel();
		}
	}

	@Override
	public String toString(){
		return label;
	}
}

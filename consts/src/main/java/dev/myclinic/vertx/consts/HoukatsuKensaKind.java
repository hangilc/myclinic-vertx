package dev.myclinic.vertx.consts;

public enum HoukatsuKensaKind {
	NONE("00"),
	KETSUEKIKAGAKU("01"),
	ENDOCRINE("02"),
	HEPATITIS("03"),
	TUMORMARKER("05"),
	COAGULO("06"),
	AUTOANTIBODY("07"),
	TOLERANCE("08"),
	ANTIBODY("09"),
	GLOBULINCLASS("10"),
	IGE("11");

	private String code;

	HoukatsuKensaKind(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public static dev.myclinic.vertx.consts.HoukatsuKensaKind fromCode(String code){
		for(dev.myclinic.vertx.consts.HoukatsuKensaKind kind: values()){
			if( kind.code.equals(code) ){
				return kind;
			}
		}
		return null;
	}
}
package dev.myclinic.vertx.scanner.wia;

public interface PropValue {
	public enum TYPE { UNKNOWN, INT, STRING }
	default TYPE getType(){ return TYPE.UNKNOWN; }
	default int getInt(){ throw new RuntimeException("Invalid PropValue access."); }
	default String getString(){ throw new RuntimeException("Invalid PropValue access."); }
}
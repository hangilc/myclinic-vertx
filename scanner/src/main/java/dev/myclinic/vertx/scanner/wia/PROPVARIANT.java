package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.WORD;

import java.util.Arrays;
import java.util.List;

public class PROPVARIANT extends Structure {

	public static class Value extends Union {
		public LONG lVal;
		public Pointer pointerValue;
		public BSTR bstrVal;
		public BSTRBLOB bstrblobValue;

		public Value(){}

		public Value(Pointer pointer){
			super(pointer);
		}

		@Override
		protected List<String> getFieldOrder(){
			return Arrays.asList("lVal", "pointerValue", "bstrVal", "bstrblobValue");
		}
	}

	public VARTYPE vt;
	public WORD reserved1;
	public WORD reserved2;
	public WORD reserved3;
	public Value value;

	public PROPVARIANT(){}

	public PROPVARIANT(Pointer pointer){
		super(pointer);
	}

	public static PROPVARIANT createVariantCLSID(CLSID clsid){
		PROPVARIANT variant = new PROPVARIANT();
		variant.vt = new VARTYPE(Variant.VT_CLSID);
		variant.value.pointerValue = clsid.getPointer();
		return variant;
	}

	public static PROPVARIANT createVariantLONG(int value){
		PROPVARIANT variant = new PROPVARIANT();
		variant.vt = new VARTYPE(Variant.VT_I4);
		variant.value.lVal = new LONG(value);
		return variant;
	}

	public static PROPVARIANT create_VT_I4(int value){
		PROPVARIANT variant = new PROPVARIANT();
		variant.vt = new VARTYPE(Variant.VT_I4);
		variant.value.lVal = new LONG(value);
		return variant;
	}

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList("vt", "reserved1", "reserved2", "reserved3", "value");
	}

	public int getVt(){
		return vt.intValue();
	}

	public LONG getUnionLONG(){
		return (LONG)value.readField("lVal");
	}

	public Pointer getUnionPointer(){
		return (Pointer)value.readField("pointerValue");
	}

	public BSTR getUnionBSTR(){
		return (BSTR)value.readField("bstrVal");
	}

	public static short getVt(Memory memory, int offset){
		return memory.getShort(offset);
	}

	public static BSTR getBSTR(Memory memory, int offset){
		Pointer pointer = memory.getPointer(offset + 8);
		return new BSTR(pointer);
	}

	public static class PropValueBase implements PropValue {
		private PropValue.TYPE type;

		public PropValueBase(PropValue.TYPE type){
			this.type = type;
		}

		@Override
		public PropValue.TYPE getType(){
			return type;
		}
	}

	public static class PropValueInt extends PropValueBase {
		private int value;

		public PropValueInt(int value){
			super(PropValue.TYPE.INT);
			this.value = value;
		}

		@Override
		public int getInt(){
			return value;
		}
	}

	public static class PropValueString extends PropValueBase {
		private String value;

		public PropValueString(String value){
			super(PropValue.TYPE.STRING);
			this.value = value;
		}

		@Override
		public String getString(){
			return value;
		}
	}

	public PropValue toPropValue(){
		switch(getVt()){
			case Variant.VT_I4: {
				return new PropValueInt(getUnionLONG().intValue());
			}
			case Variant.VT_BSTR: {
				return new PropValueString(getUnionBSTR().getValue());
			}
			default:{
				return new PropValue(){};
			}
		}
	}

}
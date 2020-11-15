package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.ULONG;

import java.util.Arrays;
import java.util.List;

public class PROPSPEC extends Structure {
	public static class Value extends Union {
		public ULONG propid;
		public WString str;

		public Value(){}

		public Value(Pointer pointer){
			super(pointer);
		}

		@Override
		protected List<String> getFieldOrder(){
			return Arrays.asList(new String[]{
				"propid", "str"
			});
		}
	}

	public static final int PRSPEC_LPWSTR = 0;
	public static final int PRSPEC_PROPID = 1;

	public ULONG kind;
	public Value value;

	public PROPSPEC(){}

	public PROPSPEC(int propid){
		this.kind = new ULONG(PRSPEC_PROPID);
		this.value.setType(ULONG.class);
		this.value.propid = new ULONG(propid);
	}

	public PROPSPEC(String name){
		this.kind = new ULONG(PRSPEC_LPWSTR);
		this.value.setType(WString.class);
		this.value.str = new WString(name);
	}

	public PROPSPEC(Pointer pointer){
		super(pointer);
	}
	// public static class Value extends Union {
	// 	public ULONG propid;
	// 	public WString str;

	// 	@Override
	// 	protected List<String> getFieldOrder(){
	// 		return Arrays.asList(new String[]{
	// 			"propid", "str"
	// 		});
	// 	}
	// }

	// public static final int PRSPEC_LPWSTR = 0;
	// public static final int PRSPEC_PROPID = 1;

	// public ULONG kind;
	// public Value value;

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList(new String[]{
			"kind", "value"
		});
	}
}
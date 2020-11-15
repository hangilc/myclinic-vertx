package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

import java.util.ArrayList;
import java.util.List;

public class PropertyWriter {

	private List<PROPSPEC> specs;
	private List<PROPVARIANT> variants;


	public PropertyWriter(){
		this.specs = new ArrayList<>();
		this.variants = new ArrayList<>();
	}

	public PropertyWriter set(int propid, LONG value){
		addSpec(propid);
		PROPVARIANT variant = new PROPVARIANT();
		variant.vt = new VARTYPE(Variant.VT_I4);
        variant.value.setType(LONG.class);
        variant.value.lVal = value;
        variants.add(variant);
        return this;
	}

	public PropertyWriter set(int propid, CLSID clsid){
		return set(propid, Variant.VT_CLSID, clsid.getPointer());
	}

	public PropertyWriter set(int propid, int vartype, Pointer pointer){
		addSpec(propid);
		PROPVARIANT variant = new PROPVARIANT();
		variant.vt = new VARTYPE(vartype);
        variant.value.setType(Pointer.class);
        variant.value.pointerValue = pointer;
        variants.add(variant);
        return this;
	}

	public void write(WiaPropertyStorage storage){
		int n = specs.size();
		PROPSPEC[] propsArray = (PROPSPEC[])new PROPSPEC().toArray(n);
		PROPVARIANT[] variantsArray = (PROPVARIANT[])new PROPVARIANT().toArray(n);
		for(int i=0;i<n;i++){
			PROPSPEC src = specs.get(i);
			PROPSPEC spec = propsArray[i];
			spec.kind = src.kind;
			if( spec.kind.intValue() == PROPSPEC.PRSPEC_PROPID ){
				spec.value.setType(ULONG.class);
				spec.value.propid = src.value.propid;
			} else if( spec.kind.intValue() == PROPSPEC.PRSPEC_PROPID ){
				spec.value.setType(WString.class);
				spec.value.str = src.value.str;
			} else {
				throw new RuntimeException("unknown PROPSPEC kind: " + spec.kind.intValue());
			}
		}
		for(int i=0;i<n;i++){
			PROPVARIANT src = variants.get(i);
			PROPVARIANT var = variantsArray[i];
			var.vt = src.vt;
			switch(var.vt.intValue()){
				case Variant.VT_I4: {
			        var.value.setType(LONG.class);
			        var.value.lVal = src.value.lVal;
			        break;
				}
				case Variant.VT_CLSID: {
					var.value.setType(Pointer.class);
					var.value.pointerValue = src.value.pointerValue;
					break;
				}
				default: {
					throw new RuntimeException("unknown PROPVARIANT vt: " + var.vt.intValue());
				}
			}
		}
        HRESULT hr = storage.WriteMultiple(new ULONG(n), propsArray, variantsArray, 
            new WiaTypes.PROPID(WiaConsts.WIA_RESERVED_FOR_NEW_PROPS));
        COMUtils.checkRC(hr);
	}

	public void write(WiaItem wiaItem){
        PointerByReference pbr = new PointerByReference();
        HRESULT hr = wiaItem.QueryInterface(new REFIID(IWiaPropertyStorage.IID_IWiaPropertyStorage), pbr);
        COMUtils.checkRC(hr);
        WiaPropertyStorage propStorage = new WiaPropertyStorage(pbr.getValue());
        write(propStorage);
        propStorage.Release();
	}

	private void addSpec(int propid){
		PROPSPEC spec = new PROPSPEC();
		spec.kind = new ULONG(PROPSPEC.PRSPEC_PROPID);
		spec.value.setType(ULONG.class);
		spec.value.propid = new ULONG(propid);
		specs.add(spec);
	}
}
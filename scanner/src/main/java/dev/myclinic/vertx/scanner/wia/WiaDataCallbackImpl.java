package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.COM.UnknownVTable;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WiaDataCallbackImpl extends Structure {

	private static Logger logger = LoggerFactory.getLogger(WiaDataCallbackImpl.class);
 	private static Set<Pointer> allocated = new HashSet<Pointer>();

	public Vtbl.ByReference lpVtbl;
	public int refCount;

	public WiaDataCallbackImpl(){}

	public WiaDataCallbackImpl(Pointer pointer){
		super(pointer);
	}

	@Override
	protected List<String> getFieldOrder(){
		return Arrays.asList("lpVtbl", "refCount");
	}

	public static interface BandedDataCallbackCallback extends StdCallCallback {
		HRESULT invoke(Pointer thisPointer, LONG lMessage, LONG lStatus, LONG lPercentComplete,
			LONG lOffset, LONG lLength, LONG lReserved, LONG lResLength, PointerByReference pbBuffer);
	}

	public static class Vtbl extends Structure {

		public static class ByReference extends Vtbl implements Structure.ByReference {
			public ByReference(){}

			public ByReference(Pointer pointer){
				super(pointer);
			}
		}

		public Vtbl(){}

		public Vtbl(Pointer pointer){
			super(pointer);
		}

		public UnknownVTable.QueryInterfaceCallback QueryInterfaceCallback;
		public UnknownVTable.AddRefCallback AddRefCallback;
		public UnknownVTable.ReleaseCallback ReleaseCallback;
		public BandedDataCallbackCallback BandedDataCallbackCallback;

		@Override
		protected List<String> getFieldOrder(){
			return Arrays.asList(new String[]{
				"QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback", "BandedDataCallbackCallback"
			});
		}
	}

	private static UnknownVTable.QueryInterfaceCallback queryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback(){
		@Override
		public HRESULT invoke(Pointer thisPointer, REFIID refid, PointerByReference ppvObject) {
			if( ppvObject == null ){
				return new HRESULT(WinError.E_POINTER);
			}
			IID iid = refid.getValue();
			if( iid.equals(IUnknown.IID_IUNKNOWN) || iid.equals(IWiaDataCallback.IID_IWiaDataCallback) ){
				logger.debug("QueryInterface called");
				ppvObject.setValue(thisPointer);
				WiaDataCallbackImpl self = new WiaDataCallbackImpl(thisPointer);
				self.read();
				self.refCount += 1;
				self.write();
				return WinError.S_OK;
			} else {
				return new HRESULT(WinError.E_NOINTERFACE);
			}
		}
	};

	private static UnknownVTable.AddRefCallback addRefCallback = new UnknownVTable.AddRefCallback(){
		@Override
		public int invoke(Pointer thisPointer){
			WiaDataCallbackImpl self = new WiaDataCallbackImpl(thisPointer);
			self.read();
			logger.debug("AddRef called with refCount {}", self.refCount);
			self.refCount += 1;
			self.write();
			logger.debug("AddRef returning {}", self.refCount);
			return self.refCount;
		}
	};

	private static UnknownVTable.ReleaseCallback releaseCallback = new UnknownVTable.ReleaseCallback(){
		@Override
		public int invoke(Pointer thisPointer){
			WiaDataCallbackImpl self = new WiaDataCallbackImpl(thisPointer);
			self.read();
			logger.debug("Release called with refCount {}", self.refCount);
			self.refCount -= 1;
			if( self.refCount < 0 ){
				throw new RuntimeException("Too many Release");
			}
			if( self.refCount == 0 ){
				allocated.remove(thisPointer);
				logger.debug("deallocated with remaining {}", allocated.size());
			}
			self.write();
			logger.debug("Release returning {}", self.refCount);
			return self.refCount;
		}
	};

	public static WiaDataCallback create(BandedDataCallbackCallback callback){
		Vtbl.ByReference lpVtbl = new Vtbl.ByReference();
		lpVtbl.QueryInterfaceCallback = queryInterfaceCallback;
		lpVtbl.AddRefCallback = addRefCallback;
		lpVtbl.ReleaseCallback = releaseCallback;
		lpVtbl.BandedDataCallbackCallback = callback;
		lpVtbl.write();
		WiaDataCallbackImpl wiaDataCallbackImpl = new WiaDataCallbackImpl();
		wiaDataCallbackImpl.lpVtbl = lpVtbl;
		wiaDataCallbackImpl.refCount = 1;
		wiaDataCallbackImpl.write();
		allocated.add(wiaDataCallbackImpl.getPointer());
		return new WiaDataCallback(wiaDataCallbackImpl.getPointer());
	}
}

package dev.myclinic.vertx.drawer.printer;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public class DevmodeInfo {
	private String deviceName = "";
	private int orientation;
	private String orientationLabel = "";
	private int paperSize;
	private String paperSizeLabel = "";
	private int copies;
	private int printQuality;
	private String printQualityLabel = "";
	private int defaultSource;
	private String defaultSourceLabel = "";

	private DevmodeInfo(){

	}

	public DevmodeInfo(Pointer pointer){
		init(pointer);
	}

	public DevmodeInfo(byte[] data){
		Pointer pointer = new Memory(data.length);
		pointer.write(0, data, 0, data.length);
		init(pointer);
	}

	private void init(Pointer pDevMode){
		DEVMODE devmode = new DEVMODE(pDevMode);
		setDeviceName(zeroTerminatedString(devmode.dmDeviceName));
		setOrientation(devmode.dmOrientation);
		setPaperSize(devmode.dmPaperSize);
		setCopies(devmode.dmCopies);
		setPrintQuality(devmode.dmPrintQuality);
		setDefaultSource(devmode.dmDefaultSource);
	}

	private String zeroTerminatedString(char[] src){
		int i;
		for(i=0;i<src.length;i++){
			if( src[i] == 0 ){
				break;
			}
		}
		return new String(src, 0, i);
	}

	public String getDeviceName(){
		return deviceName;
	}

	private void setDeviceName(String deviceName){
		this.deviceName = deviceName;
	}

	public int getOrientation(){
		return orientation;
	}

	private void setOrientation(int orientation){
		this.orientation = orientation;
		this.orientationLabel = PrinterConsts.findOrientationLabel(orientation);
	}

	public String getOrientationLabel(){
		return orientationLabel;
	}

	public int getPaperSize(){
		return paperSize;
	}

	private void setPaperSize(int paperSize){
		this.paperSize = paperSize;
		this.paperSizeLabel = PrinterConsts.findPaperSizeLabel(paperSize);
	}

	public String getPaperSizeLabel(){
		return paperSizeLabel;
	}

	public int getCopies(){
		return copies;
	}

	private void setCopies(int copies){
		this.copies = copies;
	}

	public int getPrintQuality(){
		return printQuality;
	}

	private void setPrintQuality(int printQuality){
		this.printQuality = printQuality;
		this.printQualityLabel = PrinterConsts.findPrintQualityLabel(printQuality);
	}

	public String getPrintQualityLabel(){
		return printQualityLabel;
	}

	public int getDefaultSource(){
		return defaultSource;
	}

	private void setDefaultSource(int defaultSource){
		this.defaultSource = defaultSource;
		this.defaultSourceLabel = PrinterConsts.findDefaultSourceLabel(defaultSource);
	}

	public String getDefaultSourceLabel(){
		return defaultSourceLabel;
	}

	@Override
	public String toString(){
		return "DevmodeInfo[" +
			"deviceName=" + deviceName + ", " +
			"orientation=" + orientationLabel + "(" + orientation + "), " +
			"paperSize=" + paperSizeLabel + "(" + paperSize + "), " +
			"copies=" + copies + ", " +
			"printQuality=" + printQualityLabel + "(" + printQuality + "), " +
			"defaultSource=" + defaultSourceLabel + "(" + defaultSource + ")" +
		"]";
	}
}
package dev.myclinic.vertx.scanner.wia;

import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Guid.GUID;

public class WiaConsts {
	public static final CLSID CLSID_WiaDevMgr  = new CLSID("a1f4e726-8cf1-11d1-bf92-0060081ed811");
	public static final CLSID CLSID_WiaDevMgr2 = new CLSID("B6C292BC-7C88-41ee-8B54-8EC92617E599");

	public static final int WIA_DEVINFO_ENUM_ALL   = 0x0000000F;
	public static final int WIA_DEVINFO_ENUM_LOCAL = 0x00000010;

	public static final int WIA_DIP_DEV_ID                            =  2; // 0x2;
	public static final String WIA_DIP_DEV_ID_STR                     =  "Unique Device ID";

	public static final int WIA_DIP_VEND_DESC                         =  3; // 0x3;
	public static final String WIA_DIP_VEND_DESC_STR                  =  "Manufacturer";

	public static final int WIA_DIP_DEV_DESC                          =  4; // 0x4;
	public static final String WIA_DIP_DEV_DESC_STR                   =  "Description";

	public static final int WIA_DIP_DEV_TYPE                          =  5; // 0x5;
	public static final String WIA_DIP_DEV_TYPE_STR                   =  "Type";

	public static final int WIA_DIP_PORT_NAME                         =  6; // 0x6;
	public static final String WIA_DIP_PORT_NAME_STR                  =  "Port";

	public static final int WIA_DIP_DEV_NAME                          =  7; // 0x7;
	public static final String WIA_DIP_DEV_NAME_STR                   =  "Name";

	public static final int WIA_DIP_SERVER_NAME                       =  8; // 0x8;
	public static final String WIA_DIP_SERVER_NAME_STR                =  "Server";

	public static final int WIA_DIP_REMOTE_DEV_ID                     =  9; // 0x9;
	public static final String WIA_DIP_REMOTE_DEV_ID_STR              =  "Remote Device ID";

	public static final int WIA_DIP_UI_CLSID                          = 10; // 0xa;
	public static final String WIA_DIP_UI_CLSID_STR                   =  "UI Class ID";

	public static final int WIA_DIP_HW_CONFIG                         = 11; // 0xb;
	public static final String WIA_DIP_HW_CONFIG_STR                  =  "Hardware Configuration";

	public static final int WIA_DIP_BAUDRATE                          = 12; // 0xc;
	public static final String WIA_DIP_BAUDRATE_STR                   =  "BaudRate";

	public static final int WIA_DIP_STI_GEN_CAPABILITIES              = 13; // 0xd;
	public static final String WIA_DIP_STI_GEN_CAPABILITIES_STR       =  "STI Generic Capabilities";

	public static final int WIA_DIP_WIA_VERSION                       = 14; // 0xe;
	public static final String WIA_DIP_WIA_VERSION_STR                =  "WIA Version";

	public static final int WIA_DIP_DRIVER_VERSION                    = 15; // 0xf;
	public static final String WIA_DIP_DRIVER_VERSION_STR             =  "Driver Version";

	public static final int WIA_DIP_PNP_ID                            = 16; // 0x10;
	public static final String WIA_DIP_PNP_ID_STR                     =  "PnP ID String";

	public static final int WIA_DIP_STI_DRIVER_VERSION                = 17; // 0x11;
	public static final String WIA_DIP_STI_DRIVER_VERSION_STR      	  =  "STI Driver Version";

	public static final int WIA_IPA_ITEM_NAME                         = 4098; // 0x1002
	public static final String WIA_IPA_ITEM_NAME_STR                  = "Item Name";

	public static final int WIA_IPA_FULL_ITEM_NAME                    = 4099; // 0x1003
	public static final String WIA_IPA_FULL_ITEM_NAME_STR             = "Full Item Name";

	public static final int WIA_IPA_ITEM_TIME                         = 4100; // 0x1004
	public static final String WIA_IPA_ITEM_TIME_STR                  = "Item Time Stamp";

	public static final int WIA_IPA_ITEM_FLAGS                        = 4101; // 0x1005
	public static final String WIA_IPA_ITEM_FLAGS_STR                 = "Item Flags";

	public static final int WIA_IPA_ACCESS_RIGHTS                     = 4102; // 0x1006
	public static final String WIA_IPA_ACCESS_RIGHTS_STR              = "Access Rights";

	public static final int WIA_IPA_DATATYPE                          = 4103; // 0x1007
	public static final String WIA_IPA_DATATYPE_STR                   = "Data Type";

	public static final int WIA_IPA_DEPTH                             = 4104; // 0x1008
	public static final String WIA_IPA_DEPTH_STR                      = "Bits Per Pixel";

	public static final int WIA_IPA_PREFERRED_FORMAT                  = 4105; // 0x1009
	public static final String WIA_IPA_PREFERRED_FORMAT_STR           = "Preferred Format";

	public static final int WIA_IPA_FORMAT                            = 4106; // 0x100a
	public static final String WIA_IPA_FORMAT_STR                     = "Format";

	public static final int WIA_IPA_COMPRESSION                       = 4107; // 0x100b
	public static final String WIA_IPA_COMPRESSION_STR                = "Compression";

	public static final int WIA_IPA_TYMED                             = 4108; // 0x100c
	public static final String WIA_IPA_TYMED_STR                      = "Media Type";

	public static final int WIA_IPA_CHANNELS_PER_PIXEL                = 4109; // 0x100d
	public static final String WIA_IPA_CHANNELS_PER_PIXEL_STR         = "Channels Per Pixel";

	public static final int WIA_IPA_BITS_PER_CHANNEL                  = 4110; // 0x100e
	public static final String WIA_IPA_BITS_PER_CHANNEL_STR           = "Bits Per Channel";

	public static final int WIA_IPA_PLANAR                            = 4111; // 0x100f
	public static final String WIA_IPA_PLANAR_STR                     = "Planar";

	public static final int WIA_IPA_PIXELS_PER_LINE                   = 4112; // 0x1010
	public static final String WIA_IPA_PIXELS_PER_LINE_STR            = "Pixels Per Line";

	public static final int WIA_IPA_BYTES_PER_LINE                    = 4113; // 0x1011
	public static final String WIA_IPA_BYTES_PER_LINE_STR             = "Bytes Per Line";

	public static final int WIA_IPA_NUMBER_OF_LINES                   = 4114; // 0x1012
	public static final String WIA_IPA_NUMBER_OF_LINES_STR            = "Number of Lines";

	public static final int WIA_IPA_GAMMA_CURVES                      = 4115; // 0x1013
	public static final String WIA_IPA_GAMMA_CURVES_STR               = "Gamma Curves";

	public static final int WIA_IPA_ITEM_SIZE                         = 4116; // 0x1014
	public static final String WIA_IPA_ITEM_SIZE_STR                  = "Item Size";

	public static final int WIA_IPA_COLOR_PROFILE                     = 4117; // 0x1015
	public static final String WIA_IPA_COLOR_PROFILE_STR              = "Color Profiles";

	public static final int WIA_IPA_MIN_BUFFER_SIZE                   = 4118; // 0x1016
	public static final String WIA_IPA_MIN_BUFFER_SIZE_STR            = "Buffer Size";

	public static final int WIA_IPA_BUFFER_SIZE                       = 4118; // 0x1016
	public static final String WIA_IPA_BUFFER_SIZE_STR                = "Buffer Size";

	public static final int WIA_IPA_REGION_TYPE                       = 4119; // 0x1017
	public static final String WIA_IPA_REGION_TYPE_STR                = "Region Type";

	public static final int WIA_IPA_ICM_PROFILE_NAME                  = 4120; // 0x1018
	public static final String WIA_IPA_ICM_PROFILE_NAME_STR           = "Color Profile Name";

	public static final int WIA_IPA_APP_COLOR_MAPPING                 = 4121; // 0x1019
	public static final String WIA_IPA_APP_COLOR_MAPPING_STR          = "Application Applies Color Mapping";

	public static final int WIA_IPA_PROP_STREAM_COMPAT_ID             = 4122; // 0x101a
	public static final String WIA_IPA_PROP_STREAM_COMPAT_ID_STR      = "Stream Compatibility ID";

	public static final int WIA_IPA_FILENAME_EXTENSION                = 4123; // 0x101b
	public static final String WIA_IPA_FILENAME_EXTENSION_STR         = "Filename extension";

	public static final int WIA_IPA_SUPPRESS_PROPERTY_PAGE            = 4124; // 0x101c
	public static final String WIA_IPA_SUPPRESS_PROPERTY_PAGE_STR     = "Suppress a property page";

	public static final int WIA_IPC_THUMBNAIL                         = 5122; // 0x1402
	public static final String WIA_IPC_THUMBNAIL_STR                  = "Thumbnail Data";

	public static final int WIA_IPC_THUMB_WIDTH                       = 5123; // 0x1403
	public static final String WIA_IPC_THUMB_WIDTH_STR                = "Thumbnail Width";

	public static final int WIA_IPC_THUMB_HEIGHT                      = 5124; // 0x1404
	public static final String WIA_IPC_THUMB_HEIGHT_STR               = "Thumbnail Height";

	public static final int WIA_IPC_AUDIO_AVAILABLE                   = 5125; // 0x1405
	public static final String WIA_IPC_AUDIO_AVAILABLE_STR            = "Audio Available";

	public static final int WIA_IPC_AUDIO_DATA_FORMAT                 = 5126; // 0x1406
	public static final String WIA_IPC_AUDIO_DATA_FORMAT_STR          = "Audio Format";

	public static final int WIA_IPC_AUDIO_DATA                        = 5127; // 0x1407
	public static final String WIA_IPC_AUDIO_DATA_STR                 = "Audio Data";

	public static final int WIA_IPC_NUM_PICT_PER_ROW                  = 5128; // 0x1408
	public static final String WIA_IPC_NUM_PICT_PER_ROW_STR           = "Pictures per Row";

	public static final int WIA_IPC_SEQUENCE                          = 5129; // 0x1409
	public static final String WIA_IPC_SEQUENCE_STR                   = "Sequence Number";

	public static final int WIA_IPC_TIMEDELAY                         = 5130; // 0x140a
	public static final String WIA_IPC_TIMEDELAY_STR                  = "Time Delay";

	public static final int WIA_IPS_CUR_INTENT                        = 6146; // 0x1802
	public static final String WIA_IPS_CUR_INTENT_STR                 = "Current Intent";

	public static final int WIA_IPS_XRES                              = 6147; // 0x1803
	public static final String WIA_IPS_XRES_STR                       = "Horizontal Resolution";

	public static final int WIA_IPS_YRES                              = 6148; // 0x1804
	public static final String WIA_IPS_YRES_STR                       = "Vertical Resolution";

	public static final int WIA_IPS_XPOS                              = 6149; // 0x1805
	public static final String WIA_IPS_XPOS_STR                       = "Horizontal Start Position";

	public static final int WIA_IPS_YPOS                              = 6150; // 0x1806
	public static final String WIA_IPS_YPOS_STR                       = "Vertical Start Position";

	public static final int WIA_IPS_XEXTENT                           = 6151; // 0x1807
	public static final String WIA_IPS_XEXTENT_STR                    = "Horizontal Extent";

	public static final int WIA_IPS_YEXTENT                           = 6152; // 0x1808
	public static final String WIA_IPS_YEXTENT_STR                    = "Vertical Extent";

	public static final int WIA_IPS_PHOTOMETRIC_INTERP                = 6153; // 0x1809
	public static final String WIA_IPS_PHOTOMETRIC_INTERP_STR         = "Photometric Interpretation";

	public static final int WIA_IPS_BRIGHTNESS                        = 6154; // 0x180a
	public static final String WIA_IPS_BRIGHTNESS_STR                 = "Brightness";

	public static final int WIA_IPS_CONTRAST                          = 6155; // 0x180b
	public static final String WIA_IPS_CONTRAST_STR                   = "Contrast";

	public static final int WIA_IPS_ORIENTATION                       = 6156; // 0x180c
	public static final String WIA_IPS_ORIENTATION_STR                = "Orientation";

	public static final int WIA_IPS_ROTATION                          = 6157; // 0x180d
	public static final String WIA_IPS_ROTATION_STR                   = "Rotation";

	public static final int WIA_IPS_MIRROR                            = 6158; // 0x180e
	public static final String WIA_IPS_MIRROR_STR                     = "Mirror";

	public static final int WIA_IPS_THRESHOLD                         = 6159; // 0x180f
	public static final String WIA_IPS_THRESHOLD_STR                  = "Threshold";

	public static final int WIA_IPS_INVERT                            = 6160; // 0x1810
	public static final String WIA_IPS_INVERT_STR                     = "Invert";

	public static final int WIA_IPS_WARM_UP_TIME                      = 6161; // 0x1811
	public static final String WIA_IPS_WARM_UP_TIME_STR               = "Lamp Warm up Time";

	public static final int WIA_DIP_FIRST                    =    2;
	public static final int WIA_IPA_FIRST                    = 4098;
	public static final int WIA_DPF_FIRST                    = 3330;
	public static final int WIA_IPS_FIRST                    = 6146;
	public static final int WIA_DPS_FIRST                    = 3074;
	public static final int WIA_IPC_FIRST                    = 5122;
	public static final int WIA_NUM_IPC                      =    9;
	public static final int WIA_RESERVED_FOR_NEW_PROPS       = 1024;

	// Item Types
	public static final int WiaItemTypeFree                   = 0x00000000;
	public static final int WiaItemTypeImage                  = 0x00000001;
	public static final int WiaItemTypeFile                   = 0x00000002;
	public static final int WiaItemTypeFolder                 = 0x00000004;
	public static final int WiaItemTypeRoot                   = 0x00000008;
	public static final int WiaItemTypeAnalyze                = 0x00000010;
	public static final int WiaItemTypeAudio                  = 0x00000020;
	public static final int WiaItemTypeDevice                 = 0x00000040;
	public static final int WiaItemTypeDeleted                = 0x00000080;
	public static final int WiaItemTypeDisconnected           = 0x00000100;
	public static final int WiaItemTypeHPanorama              = 0x00000200;
	public static final int WiaItemTypeVPanorama              = 0x00000400;
	public static final int WiaItemTypeBurst                  = 0x00000800;
	public static final int WiaItemTypeStorage                = 0x00001000;
	public static final int WiaItemTypeTransfer               = 0x00002000;
	public static final int WiaItemTypeGenerated              = 0x00004000;
	public static final int WiaItemTypeHasAttachments         = 0x00008000;
	public static final int WiaItemTypeVideo                  = 0x00010000;
	public static final int WiaItemTypeRemoved                = 0x80000000;
	public static final int WiaItemTypeDocument               = 0x00040000;
	public static final int WiaItemTypeProgrammableDataSource = 0x00080000;

	// public static final int WiaItemTypeMask                  = 0x800FFFFF; // Vista or newer
	// public static final int WiaItemTypeMask                  = 0x8003FFFF; // Xp

	// Intent
	public static final int WIA_INTENT_NONE                  = 0x00000000;
	public static final int WIA_INTENT_IMAGE_TYPE_COLOR      = 0x00000001;
	public static final int WIA_INTENT_IMAGE_TYPE_GRAYSCALE  = 0x00000002;
	public static final int WIA_INTENT_IMAGE_TYPE_TEXT       = 0x00000004;
	public static final int WIA_INTENT_IMAGE_TYPE_MASK       = 0x0000000F;
	public static final int WIA_INTENT_MINIMIZE_SIZE         = 0x00010000;
	public static final int WIA_INTENT_MAXIMIZE_QUALITY      = 0x00020000;
	public static final int WIA_INTENT_BEST_PREVIEW          = 0x00040000;
	public static final int WIA_INTENT_SIZE_MASK             = 0x000F0000;

	// TYMED
    public static final int TYMED_HGLOBAL	= 1;
	public static final int TYMED_FILE	= 2;
	public static final int TYMED_ISTREAM	= 4;
	public static final int TYMED_ISTORAGE	= 8;
	public static final int TYMED_GDI	= 16;
	public static final int TYMED_MFPICT	= 32;
	public static final int TYMED_ENHMF	= 64;
	public static final int TYMED_NULL	= 0;

	// Image format
	public static final GUID WiaImgFmt_UNDEFINED = defineGuid(0xb96b3ca9,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_RAWRGB    = defineGuid(0xbca48b55,0xf272,0x4371,0xb0,0xf1,0x4a,0x15,0x0d,0x05,0x7b,0xb4);
	public static final GUID WiaImgFmt_MEMORYBMP = defineGuid(0xb96b3caa,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_BMP       = defineGuid(0xb96b3cab,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_EMF       = defineGuid(0xb96b3cac,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_WMF       = defineGuid(0xb96b3cad,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_JPEG      = defineGuid(0xb96b3cae,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_PNG       = defineGuid(0xb96b3caf,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_GIF       = defineGuid(0xb96b3cb0,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_TIFF      = defineGuid(0xb96b3cb1,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_EXIF      = defineGuid(0xb96b3cb2,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_PHOTOCD   = defineGuid(0xb96b3cb3,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_FLASHPIX  = defineGuid(0xb96b3cb4,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_ICO       = defineGuid(0xb96b3cb5,0x0728,0x11d3,0x9d,0x7b,0x00,0x00,0xf8,0x1e,0xf3,0x2e);
	public static final GUID WiaImgFmt_CIFF      = defineGuid(0x9821a8ab,0x3a7e,0x4215,0x94,0xe0,0xd2,0x7a,0x46,0x0c,0x03,0xb2);
	public static final GUID WiaImgFmt_PICT      = defineGuid(0xa6bc85d8,0x6b3e,0x40ee,0xa9,0x5c,0x25,0xd4,0x82,0xe4,0x1a,0xdc);
	public static final GUID WiaImgFmt_JPEG2K    = defineGuid(0x344ee2b2,0x39db,0x4dde,0x81,0x73,0xc4,0xb7,0x5f,0x8f,0x1e,0x49);
	public static final GUID WiaImgFmt_JPEG2KX   = defineGuid(0x43e14614,0xc80a,0x4850,0xba,0xf3,0x4b,0x15,0x2d,0xc8,0xda,0x27);
	// #if (_WIN32_WINNT >= 0x0600)
	public static final GUID WiaImgFmt_RAW       = defineGuid(0x6f120719,0xf1a8,0x4e07,0x9a,0xde,0x9b,0x64,0xc6,0x3a,0x3d,0xcc);
	public static final GUID WiaImgFmt_JBIG      = defineGuid(0x41e8dd92,0x2f0a,0x43d4,0x86,0x36,0xf1,0x61,0x4b,0xa1,0x1e,0x46);
	// #endif //#if (_WIN32_WINNT >= 0x0600)

	public static final int StiDeviceTypeDefault          = 0;
    public static final int StiDeviceTypeScanner          = 1;
    public static final int StiDeviceTypeDigitalCamera    = 2;
    public static final int StiDeviceTypeStreamingVideo   = 3;

	public static final int WIA_SELECT_DEVICE_NODEFAULT        = 0x00000001;
	public static final int WIA_DEVICE_DIALOG_SINGLE_IMAGE     = 0x00000002;
	public static final int WIA_DEVICE_DIALOG_USE_COMMON_UI    = 0x00000004;

	public static final int IT_MSG_DATA_HEADER              = 0x0001;
	public static final int IT_MSG_DATA                     = 0x0002;
	public static final int IT_MSG_STATUS                   = 0x0003;
	public static final int IT_MSG_TERMINATION              = 0x0004;
	public static final int IT_MSG_NEW_PAGE                 = 0x0005;
	public static final int IT_MSG_FILE_PREVIEW_DATA        = 0x0006;
	public static final int IT_MSG_FILE_PREVIEW_DATA_HEADER = 0x0007;


	private static GUID defineGuid(int data1, int data2, int data3, int data4_0, int data4_1,
		int data4_2, int data4_3, int data4_4, int data4_5, int data4_6, int data4_7){
		GUID guid = new GUID();
		guid.Data1 = data1;
		guid.Data2 = (short)data2;
		guid.Data3 = (short)data3;
		guid.Data4 = new byte[]{(byte)data4_0, (byte)data4_1, (byte)data4_2, (byte)data4_3, (byte)data4_4, (byte)data4_5, (byte)data4_6, (byte)data4_7};
		return guid;
	}

}
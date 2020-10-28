package dev.myclinic.vertx.drawerprinterwin;

public class PrinterConsts {
    public static final int PORTRAIT  = 1;
    public static final int LANDSCAPE = 2;

    public static String findOrientationLabel(int orientation){
        switch(orientation){
            case PORTRAIT: return "portrait";
            case LANDSCAPE: return "landscape";
            default: return "unknown-portrait";
        }
    }

    public static final int DMPAPER_LETTER                          = 1;
    public static final int DMPAPER_LETTERSMALL                     = 2;
    public static final int DMPAPER_TABLOID                         = 3;
    public static final int DMPAPER_LEDGER                          = 4;
    public static final int DMPAPER_LEGAL                           = 5;
    public static final int DMPAPER_STATEMENT                       = 6;
    public static final int DMPAPER_EXECUTIVE                       = 7;
    public static final int DMPAPER_A3                              = 8;
    public static final int DMPAPER_A4                              = 9;
    public static final int DMPAPER_A4SMALL                         = 10;
    public static final int DMPAPER_A5                              = 11;
    public static final int DMPAPER_B4                              = 12;
    public static final int DMPAPER_B5                              = 13;
    public static final int DMPAPER_FOLIO                           = 14;
    public static final int DMPAPER_QUARTO                          = 15;
    public static final int DMPAPER_10X14                           = 16;
    public static final int DMPAPER_11X17                           = 17;
    public static final int DMPAPER_NOTE                            = 18;
    public static final int DMPAPER_ENV_9                           = 19;
    public static final int DMPAPER_ENV_10                          = 20;
    public static final int DMPAPER_ENV_11                          = 21;
    public static final int DMPAPER_ENV_12                          = 22;
    public static final int DMPAPER_ENV_14                          = 23;
    public static final int DMPAPER_CSHEET                          = 24;
    public static final int DMPAPER_DSHEET                          = 25;
    public static final int DMPAPER_ESHEET                          = 26;
    public static final int DMPAPER_ENV_DL                          = 27;
    public static final int DMPAPER_ENV_C5                          = 28;
    public static final int DMPAPER_ENV_C3                          = 29;
    public static final int DMPAPER_ENV_C4                          = 30;
    public static final int DMPAPER_ENV_C6                          = 31;
    public static final int DMPAPER_ENV_C65                         = 32;
    public static final int DMPAPER_ENV_B4                          = 33;
    public static final int DMPAPER_ENV_B5                          = 34;
    public static final int DMPAPER_ENV_B6                          = 35;
    public static final int DMPAPER_ENV_ITALY                       = 36;
    public static final int DMPAPER_ENV_MONARCH                     = 37;
    public static final int DMPAPER_ENV_PERSONAL                    = 38;
    public static final int DMPAPER_FANFOLD_US                      = 39;
    public static final int DMPAPER_FANFOLD_STD_GERMAN              = 40;
    public static final int DMPAPER_FANFOLD_LGL_GERMAN              = 41;
    public static final int DMPAPER_ISO_B4                          = 42;
    public static final int DMPAPER_JAPANESE_POSTCARD               = 43;
    public static final int DMPAPER_9X11                            = 44;
    public static final int DMPAPER_10X11                           = 45;
    public static final int DMPAPER_15X11                           = 46;
    public static final int DMPAPER_ENV_INVITE                      = 47;
    public static final int DMPAPER_RESERVED_48                     = 48;
    public static final int DMPAPER_RESERVED_49                     = 49;
    public static final int DMPAPER_LETTER_EXTRA                    = 50;
    public static final int DMPAPER_LEGAL_EXTRA                     = 51;
    public static final int DMPAPER_TABLOID_EXTRA                   = 52;
    public static final int DMPAPER_A4_EXTRA                        = 53;
    public static final int DMPAPER_LETTER_TRANSVERSE               = 54;
    public static final int DMPAPER_A4_TRANSVERSE                   = 55;
    public static final int DMPAPER_LETTER_EXTRA_TRANSVERSE         = 56;
    public static final int DMPAPER_A_PLUS                          = 57;
    public static final int DMPAPER_B_PLUS                          = 58;
    public static final int DMPAPER_LETTER_PLUS                     = 59;
    public static final int DMPAPER_A4_PLUS                         = 60;
    public static final int DMPAPER_A5_TRANSVERSE                   = 61;
    public static final int DMPAPER_B5_TRANSVERSE                   = 62;
    public static final int DMPAPER_A3_EXTRA                        = 63;
    public static final int DMPAPER_A5_EXTRA                        = 64;
    public static final int DMPAPER_B5_EXTRA                        = 65;
    public static final int DMPAPER_A2                              = 66;
    public static final int DMPAPER_A3_TRANSVERSE                   = 67;
    public static final int DMPAPER_A3_EXTRA_TRANSVERSE             = 68;
    public static final int DMPAPER_DBL_JAPANESE_POSTCARD           = 69;
    public static final int DMPAPER_A6                              = 70;
    public static final int DMPAPER_JENV_KAKU2                      = 71;
    public static final int DMPAPER_JENV_KAKU3                      = 72;
    public static final int DMPAPER_JENV_CHOU3                      = 73;
    public static final int DMPAPER_JENV_CHOU4                      = 74;
    public static final int DMPAPER_LETTER_ROTATED                  = 75;
    public static final int DMPAPER_A3_ROTATED                      = 76;
    public static final int DMPAPER_A4_ROTATED                      = 77;
    public static final int DMPAPER_A5_ROTATED                      = 78;
    public static final int DMPAPER_B4_JIS_ROTATED                  = 79;
    public static final int DMPAPER_B5_JIS_ROTATED                  = 80;
    public static final int DMPAPER_JAPANESE_POSTCARD_ROTATED       = 81;
    public static final int DMPAPER_DBL_JAPANESE_POSTCARD_ROTATED   = 82;
    public static final int DMPAPER_A6_ROTATED                      = 83;
    public static final int DMPAPER_JENV_KAKU2_ROTATED              = 84;
    public static final int DMPAPER_JENV_KAKU3_ROTATED              = 85;
    public static final int DMPAPER_JENV_CHOU3_ROTATED              = 86;
    public static final int DMPAPER_JENV_CHOU4_ROTATED              = 87;
    public static final int DMPAPER_B6_JIS                          = 88;
    public static final int DMPAPER_B6_JIS_ROTATED                  = 89;
    public static final int DMPAPER_12X11                           = 90;
    public static final int DMPAPER_JENV_YOU4                       = 91;
    public static final int DMPAPER_JENV_YOU4_ROTATED               = 92;
    public static final int DMPAPER_P16K                            = 93;
    public static final int DMPAPER_P32K                            = 94;
    public static final int DMPAPER_P32KBIG                         = 95;
    public static final int DMPAPER_PENV_1                          = 96;
    public static final int DMPAPER_PENV_2                          = 97;
    public static final int DMPAPER_PENV_3                          = 98;
    public static final int DMPAPER_PENV_4                          = 99;
    public static final int DMPAPER_PENV_5                          = 100;
    public static final int DMPAPER_PENV_6                          = 101;
    public static final int DMPAPER_PENV_7                          = 102;
    public static final int DMPAPER_PENV_8                          = 103;
    public static final int DMPAPER_PENV_9                          = 104;
    public static final int DMPAPER_PENV_10                         = 105;
    public static final int DMPAPER_P16K_ROTATED                    = 106;
    public static final int DMPAPER_P32K_ROTATED                    = 107;
    public static final int DMPAPER_P32KBIG_ROTATED                 = 108;
    public static final int DMPAPER_PENV_1_ROTATED                  = 109;
    public static final int DMPAPER_PENV_2_ROTATED                  = 110;
    public static final int DMPAPER_PENV_3_ROTATED                  = 111;
    public static final int DMPAPER_PENV_4_ROTATED                  = 112;
    public static final int DMPAPER_PENV_5_ROTATED                  = 113;
    public static final int DMPAPER_PENV_6_ROTATED                  = 114;
    public static final int DMPAPER_PENV_7_ROTATED                  = 115;
    public static final int DMPAPER_PENV_8_ROTATED                  = 116;
    public static final int DMPAPER_PENV_9_ROTATED                  = 117;
    public static final int DMPAPER_PENV_10_ROTATED                 = 118;

    public static String findPaperSizeLabel(int code){
        switch(code){
            case DMPAPER_LETTER              : return  "Letter 8 1/2 x 11 in";
            case DMPAPER_LETTERSMALL         : return  "Letter Small 8 1/2 x 11 in";
            case DMPAPER_TABLOID             : return  "Tabloid 11 x 17 in";
            case DMPAPER_LEDGER              : return  "Ledger 17 x 11 in";
            case DMPAPER_LEGAL               : return  "Legal 8 1/2 x 14 in";
            case DMPAPER_STATEMENT           : return  "Statement 5 1/2 x 8 1/2 in";
            case DMPAPER_EXECUTIVE           : return  "Executive 7 1/4 x 10 1/2 in";
            case DMPAPER_A3                  : return  "A3 297 x 420 mm";
            case DMPAPER_A4                  : return  "A4 210 x 297 mm";
            case DMPAPER_A4SMALL             : return  "A4 Small 210 x 297 mm";
            case DMPAPER_A5                  : return  "A5 148 x 210 mm";
            case DMPAPER_B4                  : return  "B4 (JIS) 250 x 354";
            case DMPAPER_B5                  : return  "B5 (JIS) 182 x 257 mm";
            case DMPAPER_FOLIO               : return  "Folio 8 1/2 x 13 in";
            case DMPAPER_QUARTO              : return  "Quarto 215 x 275 mm";
            case DMPAPER_10X14               : return  "10x14 in";
            case DMPAPER_11X17               : return  "11x17 in";
            case DMPAPER_NOTE                : return  "Note 8 1/2 x 11 in";
            case DMPAPER_ENV_9               : return  "Envelope #9 3 7/8 x 8 7/8";
            case DMPAPER_ENV_10              : return  "Envelope #10 4 1/8 x 9 1/2";
            case DMPAPER_ENV_11              : return  "Envelope #11 4 1/2 x 10 3/8";
            case DMPAPER_ENV_12              : return  "Envelope #12 4.3 x 11";
            case DMPAPER_ENV_14              : return  "Envelope #14 5 x 11 1/2";
            case DMPAPER_CSHEET              : return  "C size sheet";
            case DMPAPER_DSHEET              : return  "D size sheet";
            case DMPAPER_ESHEET              : return  "E size sheet";
            case DMPAPER_ENV_DL              : return  "Envelope DL 110 x 220mm";
            case DMPAPER_ENV_C5              : return  "Envelope C5 162 x 229 mm";
            case DMPAPER_ENV_C3              : return  "Envelope C3  324 x 458 mm";
            case DMPAPER_ENV_C4              : return  "Envelope C4  229 x 324 mm";
            case DMPAPER_ENV_C6              : return  "Envelope C6  114 x 162 mm";
            case DMPAPER_ENV_C65             : return  "Envelope C65 114 x 229 mm";
            case DMPAPER_ENV_B4              : return  "Envelope B4  250 x 353 mm";
            case DMPAPER_ENV_B5              : return  "Envelope B5  176 x 250 mm";
            case DMPAPER_ENV_B6              : return  "Envelope B6  176 x 125 mm";
            case DMPAPER_ENV_ITALY           : return  "Envelope 110 x 230 mm";
            case DMPAPER_ENV_MONARCH         : return  "Envelope Monarch 3.875 x 7.5 in";
            case DMPAPER_ENV_PERSONAL        : return  "6 3/4 Envelope 3 5/8 x 6 1/2 in";
            case DMPAPER_FANFOLD_US          : return  "US Std Fanfold 14 7/8 x 11 in";
            case DMPAPER_FANFOLD_STD_GERMAN  : return  "German Std Fanfold 8 1/2 x 12 in";
            case DMPAPER_FANFOLD_LGL_GERMAN  : return  "German Legal Fanfold 8 1/2 x 13 in";
            case DMPAPER_ISO_B4              : return  "B4 (ISO) 250 x 353 mm";
            case DMPAPER_JAPANESE_POSTCARD   : return  "Japanese Postcard 100 x 148 mm";
            case DMPAPER_9X11                : return  "9 x 11 in";
            case DMPAPER_10X11               : return  "10 x 11 in";
            case DMPAPER_15X11               : return  "15 x 11 in";
            case DMPAPER_ENV_INVITE          : return  "Envelope Invite 220 x 220 mm";
            case DMPAPER_RESERVED_48         : return  "RESERVED--DO NOT USE";
            case DMPAPER_RESERVED_49         : return  "RESERVED--DO NOT USE";
            case DMPAPER_LETTER_EXTRA        : return  "Letter Extra 9.3 x 12 in";
            case DMPAPER_LEGAL_EXTRA         : return  "Legal Extra 9.3 x 15 in";
            case DMPAPER_TABLOID_EXTRA       : return  "Tabloid Extra 11.69 x 18 in";
            case DMPAPER_A4_EXTRA            : return  "A4 Extra 9.27 x 12.69 in";
            case DMPAPER_LETTER_TRANSVERSE   : return  "Letter Transverse 8.3 x 11 in";
            case DMPAPER_A4_TRANSVERSE       : return  "A4 Transverse 210 x 297 mm";
            case DMPAPER_LETTER_EXTRA_TRANSVERSE : return "Letter Extra Transverse 9.3 x 12 in";
            case DMPAPER_A_PLUS              : return  "SuperA/SuperA/A4 227 x 356 mm";
            case DMPAPER_B_PLUS              : return  "SuperB/SuperB/A3 305 x 487 mm";
            case DMPAPER_LETTER_PLUS         : return  "Letter Plus 8.5 x 12.69 in";
            case DMPAPER_A4_PLUS             : return  "A4 Plus 210 x 330 mm";
            case DMPAPER_A5_TRANSVERSE       : return  "A5 Transverse 148 x 210 mm";
            case DMPAPER_B5_TRANSVERSE       : return  "B5 (JIS) Transverse 182 x 257 mm";
            case DMPAPER_A3_EXTRA            : return  "A3 Extra 322 x 445 mm";
            case DMPAPER_A5_EXTRA            : return  "A5 Extra 174 x 235 mm";
            case DMPAPER_B5_EXTRA            : return  "B5 (ISO) Extra 201 x 276 mm";
            case DMPAPER_A2                  : return  "A2 420 x 594 mm";
            case DMPAPER_A3_TRANSVERSE       : return  "A3 Transverse 297 x 420 mm";
            case DMPAPER_A3_EXTRA_TRANSVERSE : return  "A3 Extra Transverse 322 x 445 mm";
            case DMPAPER_DBL_JAPANESE_POSTCARD : return "Japanese Double Postcard 200 x 148 mm";
            case DMPAPER_A6                  : return  "A6 105 x 148 mm";
            case DMPAPER_JENV_KAKU2          : return  "Japanese Envelope Kaku #2";
            case DMPAPER_JENV_KAKU3          : return  "Japanese Envelope Kaku #3";
            case DMPAPER_JENV_CHOU3          : return  "Japanese Envelope Chou #3";
            case DMPAPER_JENV_CHOU4          : return  "Japanese Envelope Chou #4";
            case DMPAPER_LETTER_ROTATED      : return  "Letter Rotated 11 x 8 1/2 11 in";
            case DMPAPER_A3_ROTATED          : return  "A3 Rotated 420 x 297 mm";
            case DMPAPER_A4_ROTATED          : return  "A4 Rotated 297 x 210 mm";
            case DMPAPER_A5_ROTATED          : return  "A5 Rotated 210 x 148 mm";
            case DMPAPER_B4_JIS_ROTATED      : return  "B4 (JIS) Rotated 364 x 257 mm";
            case DMPAPER_B5_JIS_ROTATED      : return  "B5 (JIS) Rotated 257 x 182 mm";
            case DMPAPER_JAPANESE_POSTCARD_ROTATED : return "Japanese Postcard Rotated 148 x 100 mm";
            case DMPAPER_DBL_JAPANESE_POSTCARD_ROTATED : return "Double Japanese Postcard Rotated 148 x 200 mm";
            case DMPAPER_A6_ROTATED          : return  "A6 Rotated 148 x 105 mm";
            case DMPAPER_JENV_KAKU2_ROTATED  : return  "Japanese Envelope Kaku #2 Rotated";
            case DMPAPER_JENV_KAKU3_ROTATED  : return  "Japanese Envelope Kaku #3 Rotated";
            case DMPAPER_JENV_CHOU3_ROTATED  : return  "Japanese Envelope Chou #3 Rotated";
            case DMPAPER_JENV_CHOU4_ROTATED  : return  "Japanese Envelope Chou #4 Rotated";
            case DMPAPER_B6_JIS              : return  "B6 (JIS) 128 x 182 mm";
            case DMPAPER_B6_JIS_ROTATED      : return  "B6 (JIS) Rotated 182 x 128 mm";
            case DMPAPER_12X11               : return  "12 x 11 in";
            case DMPAPER_JENV_YOU4           : return  "Japanese Envelope You #4";
            case DMPAPER_JENV_YOU4_ROTATED   : return  "Japanese Envelope You #4 Rotated";
            case DMPAPER_P16K                : return  "PRC 16K 146 x 215 mm";
            case DMPAPER_P32K                : return  "PRC 32K 97 x 151 mm";
            case DMPAPER_P32KBIG             : return  "PRC 32K(Big) 97 x 151 mm";
            case DMPAPER_PENV_1              : return  "PRC Envelope #1 102 x 165 mm";
            case DMPAPER_PENV_2              : return  "PRC Envelope #2 102 x 176 mm";
            case DMPAPER_PENV_3              : return  "PRC Envelope #3 125 x 176 mm";
            case DMPAPER_PENV_4              : return  "PRC Envelope #4 110 x 208 mm";
            case DMPAPER_PENV_5              : return "PRC Envelope #5 110 x 220 mm";
            case DMPAPER_PENV_6              : return "PRC Envelope #6 120 x 230 mm";
            case DMPAPER_PENV_7              : return "PRC Envelope #7 160 x 230 mm";
            case DMPAPER_PENV_8              : return "PRC Envelope #8 120 x 309 mm";
            case DMPAPER_PENV_9              : return "PRC Envelope #9 229 x 324 mm";
            case DMPAPER_PENV_10             : return "PRC Envelope #10 324 x 458 mm";
            case DMPAPER_P16K_ROTATED        : return "PRC 16K Rotated";
            case DMPAPER_P32K_ROTATED        : return "PRC 32K Rotated";
            case DMPAPER_P32KBIG_ROTATED     : return "PRC 32K(Big) Rotated";
            case DMPAPER_PENV_1_ROTATED      : return "PRC Envelope #1 Rotated 165 x 102 mm";
            case DMPAPER_PENV_2_ROTATED      : return "PRC Envelope #2 Rotated 176 x 102 mm";
            case DMPAPER_PENV_3_ROTATED      : return "PRC Envelope #3 Rotated 176 x 125 mm";
            case DMPAPER_PENV_4_ROTATED      : return "PRC Envelope #4 Rotated 208 x 110 mm";
            case DMPAPER_PENV_5_ROTATED      : return "PRC Envelope #5 Rotated 220 x 110 mm";
            case DMPAPER_PENV_6_ROTATED      : return "PRC Envelope #6 Rotated 230 x 120 mm";
            case DMPAPER_PENV_7_ROTATED      : return "PRC Envelope #7 Rotated 230 x 160 mm";
            case DMPAPER_PENV_8_ROTATED      : return "PRC Envelope #8 Rotated 309 x 120 mm";
            case DMPAPER_PENV_9_ROTATED      : return "PRC Envelope #9 Rotated 324 x 229 mm";
            case DMPAPER_PENV_10_ROTATED     : return "PRC Envelope #10 Rotated 458 x 324 mm";
            default: return "" + code;          
        }
    }

    public static final int QUALITY_DRAFT   = -1;
    public static final int QUALITY_LOW     = -2;
    public static final int QUALITY_MEDIUM  = -3;
    public static final int QUALITY_HIGH    = -4;

    public static String findPrintQualityLabel(int code){
        if( code > 0 ){
            return code + "dpi";
        } else {
            switch(code){
                case QUALITY_DRAFT: return "draft";
                case QUALITY_LOW: return "low";
                case QUALITY_MEDIUM: return "medium";
                case QUALITY_HIGH: return "high";
                default: return "unknown-print-quality";
            }
        }
    }

    public static final int DMBIN_UPPER = 1;
    public static final int DMBIN_LOWER = 2;
    public static final int DMBIN_MIDDLE = 3;
    public static final int DMBIN_MANUAL = 4;
    public static final int DMBIN_ENVELOPE = 5;
    public static final int DMBIN_ENVMANUAL = 6;
    public static final int DMBIN_AUTO = 7;
    public static final int DMBIN_TRACTOR = 8;
    public static final int DMBIN_SMALLFMT = 9;
    public static final int DMBIN_LARGEFMT = 10;
    public static final int DMBIN_LARGECAPACITY = 11;
    public static final int DMBIN_CASSETTE = 14;
    public static final int DMBIN_FORMSOURCE = 15;

    public static String findDefaultSourceLabel(int code){
        switch(code){
            case DMBIN_UPPER         : return "upper";
            case DMBIN_LOWER         : return "lower";
            case DMBIN_MIDDLE        : return "middle";
            case DMBIN_MANUAL        : return "manual";
            case DMBIN_ENVELOPE      : return "envelope";
            case DMBIN_ENVMANUAL     : return "envmanual";
            case DMBIN_AUTO          : return "auto";
            case DMBIN_TRACTOR       : return "tractor";
            case DMBIN_SMALLFMT      : return "smallfmt";
            case DMBIN_LARGEFMT      : return "largefmt";
            case DMBIN_LARGECAPACITY : return "largecapacity";
            case DMBIN_CASSETTE      : return "cassette";
            case DMBIN_FORMSOURCE    : return "formsource";
            default: return "unknown-default-source";
        }
    }

    public static final int LF_FACESIZE = 32;


    public static final int OUT_DEFAULT_PRECIS        =  0;
    public static final int OUT_STRING_PRECIS         =  1;
    public static final int OUT_CHARACTER_PRECIS      =  2;
    public static final int OUT_STROKE_PRECIS         =  3;
    public static final int OUT_TT_PRECIS             =  4;
    public static final int OUT_DEVICE_PRECIS         =  5;
    public static final int OUT_RASTER_PRECIS         =  6;
    public static final int OUT_TT_ONLY_PRECIS        =  7;
    public static final int OUT_OUTLINE_PRECIS        =  8;
    public static final int OUT_SCREEN_OUTLINE_PRECIS =  9;
    public static final int OUT_PS_ONLY_PRECIS         = 10;

    public static final int CLIP_DEFAULT_PRECIS   =  0;
    public static final int CLIP_CHARACTER_PRECIS =  1;
    public static final int CLIP_STROKE_PRECIS    =  2;
    public static final int CLIP_MASK             =  0xf;
    public static final int CLIP_LH_ANGLES        =  (1<<4);
    public static final int CLIP_TT_ALWAYS        =  (2<<4);
    public static final int CLIP_DFA_DISABLE      =  (4<<4);
    public static final int CLIP_EMBEDDED         =  (8<<4);

    public static final int DEFAULT_QUALITY        = 0;
    public static final int DRAFT_QUALITY          = 1;
    public static final int PROOF_QUALITY          = 2;
    public static final int NONANTIALIASED_QUALITY = 3;
    public static final int ANTIALIASED_QUALITY    = 4;

    public static final int CLEARTYPE_QUALITY             = 5;
    public static final int CLEARTYPE_NATURAL_QUALITY     = 6;

    public static final int DEFAULT_PITCH         = 0;
    public static final int FIXED_PITCH           = 1;
    public static final int VARIABLE_PITCH        = 2;
    public static final int MONO_FONT             = 8;

    public static final int ANSI_CHARSET          = 0;
    public static final int DEFAULT_CHARSET       = 1;
    public static final int SYMBOL_CHARSET        = 2;
    public static final int SHIFTJIS_CHARSET      = 128;
    public static final int HANGEUL_CHARSET       = 129;
    public static final int HANGUL_CHARSET        = 129;
    public static final int GB2312_CHARSET        = 134;
    public static final int CHINESEBIG5_CHARSET   = 136;
    public static final int OEM_CHARSET           = 255;
    public static final int JOHAB_CHARSET         = 130;
    public static final int HEBREW_CHARSET        = 177;
    public static final int ARABIC_CHARSET        = 178;
    public static final int GREEK_CHARSET         = 161;
    public static final int TURKISH_CHARSET       = 162;
    public static final int VIETNAMESE_CHARSET    = 163;
    public static final int THAI_CHARSET          = 222;
    public static final int EASTEUROPE_CHARSET    = 238;
    public static final int RUSSIAN_CHARSET       = 204;

    public static final int MAC_CHARSET           =  77;
    public static final int BALTIC_CHARSET        = 186;

    public static final long FS_LATIN1             = 0x00000001L;
    public static final long FS_LATIN2             = 0x00000002L;
    public static final long FS_CYRILLIC           = 0x00000004L;
    public static final long FS_GREEK              = 0x00000008L;
    public static final long FS_TURKISH            = 0x00000010L;
    public static final long FS_HEBREW             = 0x00000020L;
    public static final long FS_ARABIC             = 0x00000040L;
    public static final long FS_BALTIC             = 0x00000080L;
    public static final long FS_VIETNAMESE         = 0x00000100L;
    public static final long FS_THAI               = 0x00010000L;
    public static final long FS_JISJAPAN           = 0x00020000L;
    public static final long FS_CHINESESIMP        = 0x00040000L;
    public static final long FS_WANSUNG            = 0x00080000L;
    public static final long FS_CHINESETRAD        = 0x00100000L;
    public static final long FS_JOHAB              = 0x00200000L;
    public static final long FS_SYMBOL             = 0x80000000L;

    public static final int FF_DONTCARE       = (0<<4);
    public static final int FF_ROMAN          = (1<<4);
    public static final int FF_SWISS          = (2<<4);
    public static final int FF_MODERN         = (3<<4);
    public static final int FF_SCRIPT         = (4<<4);
    public static final int FF_DECORATIVE     = (5<<4);


    public static final int FW_DONTCARE       =   0;
    public static final int FW_THIN           = 100;
    public static final int FW_EXTRALIGHT     = 200;
    public static final int FW_LIGHT          = 300;
    public static final int FW_NORMAL         = 400;
    public static final int FW_MEDIUM         = 500;
    public static final int FW_SEMIBOLD       = 600;
    public static final int FW_BOLD           = 700;
    public static final int FW_EXTRABOLD      = 800;
    public static final int FW_HEAVY          = 900;

    public static final int FW_ULTRALIGHT     = FW_EXTRALIGHT;
    public static final int FW_REGULAR        = FW_NORMAL;
    public static final int FW_DEMIBOLD       = FW_SEMIBOLD;
    public static final int FW_ULTRABOLD      = FW_EXTRABOLD;
    public static final int FW_BLACK          = FW_HEAVY;

    public static final int LOGPIXELSX  = 88;
    public static final int LOGPIXELSY  = 90;

    public static final int OPQAUE = 2;
    public static final int TRANSPARENT = 1;

    public static final int PS_SOLID            = 0;
    public static final int PS_DASH             = 1;
    public static final int PS_DOT              = 2;
    public static final int PS_DASHDOT          = 3;
    public static final int PS_DASHDOTDOT       = 4;
    public static final int PS_NULL             = 5;
    public static final int PS_INSIDEFRAME      = 6;
    public static final int PS_USERSTYLE        = 7;
    public static final int PS_ALTERNATE        = 8;
    public static final int PS_STYLE_MASK       = 0x0000000F;

    public static final int PS_ENDCAP_ROUND     = 0x00000000;
    public static final int PS_ENDCAP_SQUARE    = 0x00000100;
    public static final int PS_ENDCAP_FLAT      = 0x00000200;
    public static final int PS_ENDCAP_MASK      = 0x00000F00;

    public static final int PS_JOIN_ROUND       = 0x00000000;
    public static final int PS_JOIN_BEVEL       = 0x00001000;
    public static final int PS_JOIN_MITER       = 0x00002000;
    public static final int PS_JOIN_MASK        = 0x0000F000;


    public static final int PS_COSMETIC         = 0x00000000;
    public static final int PS_GEOMETRIC        = 0x00010000;
    public static final int PS_TYPE_MASK        = 0x000F0000;

    public static final int BLACK_BRUSH			= 4;
    public static final int DKGRAY_BRUSH		= 3;
    public static final int GRAY_BRUSH			= 2;
    public static final int HOLLOW_BRUSH		= 5;
    public static final int LTGRAY_BRUSH		= 1;
    public static final int NULL_BRUSH			= 5;
    public static final int WHITE_BRUSH			= 0;

    public static final int BS_SOLID            = 0;
    public static final int BS_NULL             = 1;
    public static final int BS_HOLLOW           = BS_NULL;
    public static final int BS_HATCHED          = 2;
    public static final int BS_PATTERN          = 3;
    public static final int BS_INDEXED          = 4;
    public static final int BS_DIBPATTERN       = 5;
    public static final int BS_DIBPATTERNPT     = 6;
    public static final int BS_PATTERN8X8       = 7;
    public static final int BS_DIBPATTERN8X8    = 8;
    public static final int BS_MONOPATTERN      = 9;

}
package dev.myclinic.vertx.consts;


public class MyclinicConsts {

	public static final int WqueueStateWaitExam = 0;
	public static final int WqueueStateInExam = 1;
	public static final int WqueueStateWaitCashier = 2;
	public static final int WqueueStateWaitDrug = 3;
	public static final int WqueueStateWaitReExam = 4;
	public static final int WqueueStateWaitAppoint = 5;
    
	public static final int PharmaQueueStateWaitPack = 0;
	public static final int PharmaQueueStateInPack   = 1;
	public static final int PharmaQueueStatePackDone = 2;

	public static final String DiseaseEndReasonNotEnded = "N";
	public static final String DiseaseEndReasonCured = "C";
	public static final String DiseaseEndReasonStopped = "S";
	public static final String DiseaseEndReasonDead = "D";

	public static final int DrugCategoryNaifuku = 0;
	public static final int DrugCategoryTonpuku = 1;
	public static final int DrugCategoryGaiyou  = 2;
	public static final int DrugCategoryInjection  = 3;

	public static final int ConductKindHikaChuusha = 0;
	public static final int ConductKindJoumyakuChuusha = 1;
	public static final int ConductKindOtherChuusha = 2;
	public static final int ConductKindGazou = 3;

	public static final char ZaikeiNaifuku = '1';
	public static final char ZaikeiOther = '3';
	public static final char ZaikeiChuusha = '4';
	public static final char ZaikeiGaiyou = '6';
	public static final char ZaikeiShikaYakuzai = '8';
	public static final char ZaikeiShikaTokutei = '9';

	public static final int SmallestPostfixShuushokugoCode = 8000;
	public static final int LargestPostfixShuushookugoCode = 8999;

	public static final String[] MeisaiSections = new String[]{
        "初・再診料", "医学管理等", "在宅医療", "検査", "画像診断",
        "投薬", "注射", "処置", "その他"       
    };

	public static final String SHUUKEI_SHOSHIN = "110";
	public static final String SHUUKEI_SAISHIN_SAISHIN = "120";
	public static final String SHUUKEI_SAISHIN_GAIRAIKANRI = "122";
	public static final String SHUUKEI_SAISHIN_JIKANGAI = "123";
	public static final String SHUUKEI_SAISHIN_KYUUJITSU = "124";
	public static final String SHUUKEI_SAISHIN_SHINYA = "125";
	public static final String SHUUKEI_SHIDOU = "130";
	public static final String SHUUKEI_ZAITAKU = "140";
	public static final String SHUUKEI_TOUYAKU_NAIFUKUTONPUKUCHOUZAI = "210";
	public static final String SHUUKEI_TOUYAKU_GAIYOUCHOUZAI = "230";
	public static final String SHUUKEI_TOUYAKU_SHOHOU = "250";
	public static final String SHUUKEI_TOUYAKU_MADOKU = "260";
	public static final String SHUUKEI_TOUYAKU_CHOUKI = "270";
	public static final String SHUUKEI_CHUUSHA_SEIBUTSUETC = "300";
	public static final String SHUUKEI_CHUUSHA_HIKA = "311";
	public static final String SHUUKEI_CHUUSHA_JOUMYAKU = "321";
	public static final String SHUUKEI_CHUUSHA_OTHERS = "331";
	public static final String SHUUKEI_SHOCHI = "400";
	public static final String SHUUKEI_SHUJUTSU_SHUJUTSU = "500";
	public static final String SHUUKEI_SHUJUTSU_YUKETSU = "502";
	public static final String SHUUKEI_MASUI = "540";
	public static final String SHUUKEI_KENSA = "600";
	public static final String SHUUKEI_GAZOUSHINDAN = "700";
	public static final String SHUUKEI_OTHERS = "800";

	public static final String HOUKATSU_NONE = "00";
	public static final String HOUKATSU_KETSUEKIKAGAKU = "01";
	public static final String HOUKATSU_ENDOCRINE = "02";
	public static final String HOUKATSU_HEPATITIS = "03";
	public static final String HOUKATSU_TUMOR = "04";
	public static final String HOUKATSU_TUMORMISC = "05";
	public static final String HOUKATSU_COAGULO = "06";
	public static final String HOUKATSU_AUTOANTIBODY = "07";
	public static final String HOUKATSU_TOLERANCE = "08";
	public static final String HOUKATSU_VIRUSTITER = "09";
	public static final String HOUKATSU_VIRUSGLOBULIN = "10";
	public static final String HOUKATSU_SPECIFICIGE = "11";

}

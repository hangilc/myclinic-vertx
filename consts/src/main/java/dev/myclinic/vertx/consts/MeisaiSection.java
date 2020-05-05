package dev.myclinic.vertx.consts;

public enum MeisaiSection {
	ShoshinSaisin("初・再診料"),
	IgakuKanri("医学管理等"),
	Zaitaku("在宅医療"),
	Kensa("検査"),
	Gazou("画像診断"),
	Touyaku("投薬"),
	Chuusha("注射"),
	Shochi("処置"),
	Sonota("その他");

	private String label;

	MeisaiSection(String label){
		this.label = label;
	}

	public String getLabel(){
		return label;
	}

	public static dev.myclinic.vertx.consts.MeisaiSection fromShuukei(String shuukei){
		switch(shuukei){
			case MyclinicConsts.SHUUKEI_SHOSHIN:
			case MyclinicConsts.SHUUKEI_SAISHIN_SAISHIN:
			case MyclinicConsts.SHUUKEI_SAISHIN_GAIRAIKANRI:
			case MyclinicConsts.SHUUKEI_SAISHIN_JIKANGAI:
			case MyclinicConsts.SHUUKEI_SAISHIN_KYUUJITSU:
			case MyclinicConsts.SHUUKEI_SAISHIN_SHINYA:
				return ShoshinSaisin;
			case MyclinicConsts.SHUUKEI_SHIDOU:
				return IgakuKanri;
			case MyclinicConsts.SHUUKEI_ZAITAKU:
				return Zaitaku;
			case MyclinicConsts.SHUUKEI_KENSA:
				return Kensa;
			case MyclinicConsts.SHUUKEI_GAZOUSHINDAN:
				return Gazou;
			case MyclinicConsts.SHUUKEI_TOUYAKU_NAIFUKUTONPUKUCHOUZAI:
			case MyclinicConsts.SHUUKEI_TOUYAKU_GAIYOUCHOUZAI:
			case MyclinicConsts.SHUUKEI_TOUYAKU_SHOHOU:
			case MyclinicConsts.SHUUKEI_TOUYAKU_MADOKU:
			case MyclinicConsts.SHUUKEI_TOUYAKU_CHOUKI:
				return Touyaku;
			case MyclinicConsts.SHUUKEI_CHUUSHA_SEIBUTSUETC:
			case MyclinicConsts.SHUUKEI_CHUUSHA_HIKA:
			case MyclinicConsts.SHUUKEI_CHUUSHA_JOUMYAKU:
			case MyclinicConsts.SHUUKEI_CHUUSHA_OTHERS:
				return Chuusha;
			case MyclinicConsts.SHUUKEI_SHOCHI:
				return Shochi;
			case MyclinicConsts.SHUUKEI_SHUJUTSU_SHUJUTSU:
			case MyclinicConsts.SHUUKEI_SHUJUTSU_YUKETSU:
			case MyclinicConsts.SHUUKEI_MASUI:
			case MyclinicConsts.SHUUKEI_OTHERS:
			default: return Sonota;
		}
	}
}
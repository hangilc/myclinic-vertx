package dev.myclinic.vertx.meisai;

import dev.myclinic.vertx.consts.DrugCategory;
import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.consts.MeisaiSection;
import dev.myclinic.vertx.consts.MyclinicConsts;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.houkatsukensa.HoukatsuKensa;

import java.util.List;

public class RcptVisit {

	private Meisai meisai = new Meisai();

	public Meisai getMeisai(){
		return meisai;
	}

	public void addShinryouList(List<ShinryouFullDTO> shinryouList, HoukatsuKensa.Revision revision){
		shinryouList.forEach(shinryou -> addShinryou(shinryou, revision));
	}

	public void addDrugs(List<DrugFullDTO> drugs){
		drugs.forEach(this::addDrug);
	}

	public void addConducts(List<ConductFullDTO> conducts){
		conducts.forEach(conduct -> {
			MeisaiSection sect;
			if( conduct.conduct.kind == MyclinicConsts.ConductKindGazou ){
				sect = MeisaiSection.Gazou;
			} else {
				//sect = MeisaiSection.Chuusha;
				sect = MeisaiSection.Shochi;
			}
			conduct.conductShinryouList.forEach(conductShinryou -> addConductShinryou(conductShinryou, sect));
			conduct.conductDrugs.forEach(drug -> addConductDrug(drug, sect));
			conduct.conductKizaiList.forEach(conductKizai -> addConductKizai(conductKizai, sect));
		});
	}

	public void addShinryou(ShinryouFullDTO shinryou, HoukatsuKensa.Revision revision){
		ShinryouMasterDTO master = shinryou.master;
		HoukatsuKensaKind kind = HoukatsuKensaKind.fromCode(master.houkatsukensa);
		if( kind == HoukatsuKensaKind.NONE ){
			SimpleShinryouItem item = new SimpleShinryouItem(master.shinryoucode, master.tensuu, master.name);
			MeisaiSection section = MeisaiSection.fromShuukei(master.shuukeisaki);
			meisai.add(section, item);
		} else {
			HoukatsuKensaItem item = new HoukatsuKensaItem(kind, master, revision);
			meisai.add(MeisaiSection.Kensa, item);
		}
	}

	public void addDrug(DrugFullDTO drugFull){
		DrugDTO drug = drugFull.drug;
		IyakuhinMasterDTO master = drugFull.master;
		DrugCategory category = DrugCategory.fromCode(drug.category);
		switch(category){
			case Naifuku: {
				NaifukuItem item = new NaifukuItem(drugFull);
				meisai.add(MeisaiSection.Touyaku, item);
				break;
			}
			case Tonpuku: {
				TonpukuItem item = new TonpukuItem(drugFull);
				meisai.add(MeisaiSection.Touyaku, item);
				break;
			}
			case Gaiyou: {
				GaiyouItem item = new GaiyouItem(drugFull);
				meisai.add(MeisaiSection.Touyaku, item);
				break;
			}
			default: System.out.println("Unknown category (neglected): " + drug.category);
		}
	}

	public void addConductShinryou(ConductShinryouFullDTO conductShinryou, MeisaiSection sect){
		ShinryouMasterDTO master = conductShinryou.master;
		SimpleShinryouItem item = new SimpleShinryouItem(master.shinryoucode, master.tensuu, master.name);
		meisai.add(sect, item);
	}

	public void addConductDrug(ConductDrugFullDTO conductDrug, MeisaiSection sect){
		ConductDrugItem item = new ConductDrugItem(conductDrug, sect);
		meisai.add(sect, item);
	}

	public void addConductKizai(ConductKizaiFullDTO conductKizai, MeisaiSection sect){
		KizaiItem item = new KizaiItem(conductKizai);
		meisai.add(sect, item);
	}

}
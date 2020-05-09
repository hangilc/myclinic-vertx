package dev.myclinic.vertx.meisai;

import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.util.RcptUtil;

import java.text.DecimalFormat;

class KizaiItem extends SectionItem {

	private ConductKizaiFullDTO kizaiFull;

	KizaiItem(ConductKizaiFullDTO kizaiFull){
		super(1);
		this.kizaiFull = kizaiFull;
	}

	@Override
	public int getTanka(){
		return RcptUtil.kizaiKingakuToTen(kizaiFull.master.kingaku * kizaiFull.conductKizai.amount);
	}

	@Override
	public String getLabel(){
		DecimalFormat fmt = new DecimalFormat();
		return String.format("%s %s%s", kizaiFull.master.name,
			fmt.format(kizaiFull.conductKizai.amount), 
			kizaiFull.master.unit);
	}

}
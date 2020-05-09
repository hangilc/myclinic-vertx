package dev.myclinic.vertx.meisai;

import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.util.RcptUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class NaifukuItem extends SectionItem {

	private String usage;
	private List<DrugFullDTO> drugFullList = new ArrayList<>();

	NaifukuItem(DrugFullDTO drugFull){
		super(drugFull.drug.days);
		usage = drugFull.drug.usage;
		usage = usage.replace("就寝前", "寝る前");
		drugFullList.add(drugFull);
	}

	@Override
	public int getTanka(){
		double kingaku = drugFullList.stream()
		.collect(Collectors.summingDouble(d -> d.drug.amount * d.master.yakka));
		return RcptUtil.touyakuKingakuToTen(kingaku);
	}

	@Override
	public String getLabel(){
		DecimalFormat fmt = new DecimalFormat();
		return drugFullList.stream()
		.map(d -> {
			return String.format("%s %s%s", d.master.name, fmt.format(d.drug.amount), d.master.unit);
		})
		.collect(Collectors.joining("、"));
	}

	@Override
	public boolean canExtend(Object arg){
		if( arg != null && arg.getClass() == getClass() ){
			NaifukuItem item = (NaifukuItem)arg;
			return usage.equals(item.usage) && getCount() == item.getCount();
		}
		return false;
	}

	@Override
	public void extend(Object arg){
		NaifukuItem item = (NaifukuItem)arg;
		drugFullList.addAll(item.drugFullList);
	}

}
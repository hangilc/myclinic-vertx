package dev.myclinic.vertx.meisai;

import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import dev.myclinic.vertx.dto.ShinryouMasterDTO;
import dev.myclinic.vertx.houkatsukensa.HoukatsuKensa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class HoukatsuKensaItem extends SectionItem {

	private HoukatsuKensaKind kind;
	private HoukatsuKensa.Revision revision;
	private List<ShinryouMasterDTO> masters = new ArrayList<>();

	HoukatsuKensaItem(HoukatsuKensaKind kind, ShinryouMasterDTO master, HoukatsuKensa.Revision revision){
		this.kind = kind;
		this.revision = revision;
		masters.add(master);
	}

	@Override
	public int getTanka(){
		if( revision == null ){
			return sumMasters();
		}
		Optional<Integer> tanka = revision.calcTen(kind, masters.size());
		return tanka.orElseGet(this::sumMasters);
	}

	@Override
	public String getLabel(){
		return masters.stream()
		.map(m -> m.name)
		.collect(Collectors.joining("・"));
	}

	@Override
	public boolean canExtend(Object arg){
		if( arg != null && arg.getClass() == getClass() ){
			HoukatsuKensaItem item = (HoukatsuKensaItem)arg;
			return kind == item.kind;
		}
		return false;
	}

	@Override
	public void extend(Object arg){
		HoukatsuKensaItem item = (HoukatsuKensaItem)arg;
		masters.addAll(item.masters);
	}

	private Integer sumMasters(){
		return masters.stream()
		.collect(Collectors.summingInt(m -> m.tensuu));
	}

}
package dev.myclinic.vertx.meisai;

import dev.myclinic.vertx.consts.MeisaiSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Meisai {
	private Map<MeisaiSection, List<SectionItem>> map = new HashMap<>();

	public void add(MeisaiSection section, SectionItem item){
		List<SectionItem> bind = map.get(section);
		if( bind == null ){
			bind = new ArrayList<SectionItem>();
			map.put(section, bind);
		}
		for(SectionItem si: bind){
			if( si.canExtend(item) ){
				si.extend(item);
				return;
			}
		}
		bind.add(item);
	}

	public List<SectionItem> getItems(MeisaiSection section){
		return map.get(section);
	}

	public int sectionTotal(MeisaiSection section){
		List<SectionItem> items = getItems(section);
		if( items == null ){
			return 0;
		} else {
			return SectionItem.sum(items);
		}
	}

	public int totalTen(){
		int total = 0;
		for(List<SectionItem> items: map.values()){
			total += SectionItem.sum(items);
		}
		return total;
	}
}
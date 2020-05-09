package dev.myclinic.vertx.meisai;

import java.util.List;

public abstract class SectionItem {
	private int count;

	SectionItem(){
		this(1);
	}

	SectionItem(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	protected void incCount(int n){
		count += n;
	}

	public abstract int getTanka();
	public abstract String getLabel();

	public boolean canExtend(Object arg){
		return false;
	}

	public void extend(Object arg){
		throw new RuntimeException("cannot happen");
	}

	public static int sum(List<SectionItem> items){
		if( items == null ){
			return 0;
		} else {
			return items.stream().mapToInt(item -> item.getTanka() * item.getCount()).sum();
		}
	}

	@Override
	public String toString(){
		return "SectionItemDTO[" +
			"tanka=" + getTanka() + "," +
			"count=" + getCount() + "," +
			"label=" + getLabel() + //"," +
		"]";
	}

}

/*
import java.util.List;
import java.util.stream.Collectors;

public abstract class SectionItemDTO {

	private final Object key;
	private int count;

	SectionItemDTO(Object key){
		this.key = key;
		this.count = 1;
	}

	public Object getKey(){
		return key;
	}

	public boolean canMerge(SectionItemDTO item){
		return getKey().equals(item.getKey());
	}

	public void merge(SectionItemDTO item){
		if( !canMerge(item) ){
			throw new RuntimeException("inconsistent key");
		}
		setCount(getCount() + item.getCount());
	}

	abstract int getTanka();
	abstract String getLabel();

	public int getCount(){
		return count;
	}

	public void setCount(int count){
		this.count = count;
	}

	public static int sum(List<SectionItemDTO> items){
		return items.stream()
		.collect(Collectors.summingInt(item -> item.getTanka() * item.getCount()));
	}

	@Override
	public String toString(){
		return "SectionItemDTO[" +
			"tanka=" + getTanka() + "," +
			"count=" + getCount() + "," +
			"label=" + getLabel() + //"," +
		"]";
	}

}
*/
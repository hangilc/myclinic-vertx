package dev.myclinic.vertx.meisai;

class SimpleShinryouItem extends SectionItem {

	private int shinryoucode;
	private int ten;
	private String label;

	SimpleShinryouItem(int shinryoucode, int ten, String label){
		this.shinryoucode = shinryoucode;
		this.ten = ten;
		this.label = label;
	}

	@Override
	public int getTanka(){
		return ten;
	}

	@Override
	public String getLabel(){
		return label;
	}

	@Override
	public boolean canExtend(Object arg){
		if( arg != null && arg.getClass() == getClass() ){
			SimpleShinryouItem item = (SimpleShinryouItem)arg;
			return shinryoucode == item.shinryoucode;
		}
		return false;
	}

	@Override
	public void extend(Object arg){
		SimpleShinryouItem item = (SimpleShinryouItem)arg;
		incCount(item.getCount());
	}

}
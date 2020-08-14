package dev.myclinic.vertx.dto;

import java.util.List;

public class HokenListDTO {
	public List<ShahokokuhoDTO> shahokokuhoList;
	public List<KoukikoureiDTO> koukikoureiList;
	public List<RoujinDTO> roujinList;
	public List<KouhiDTO> kouhiList;

	@Override
	public String toString() {
		return "HokenListDTO{" +
				"shahokokuhoList=" + shahokokuhoList +
				", koukikoureiList=" + koukikoureiList +
				", roujinList=" + roujinList +
				", kouhiList=" + kouhiList +
				'}';
	}
}
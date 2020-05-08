package dev.myclinic.vertx.dto;

import java.util.List;

public class HokenListDTO {
	public List<ShahokokuhoDTO> shahokokuhoListDTO;
	public List<KoukikoureiDTO> koukikoureiListDTO;
	public List<RoujinDTO> roujinListDTO;
	public List<KouhiDTO> kouhiListDTO;

	@Override
	public String toString() {
		return "HokenListDTO{" +
				"shahokokuhoListDTO=" + shahokokuhoListDTO +
				", koukikoureiListDTO=" + koukikoureiListDTO +
				", roujinListDTO=" + roujinListDTO +
				", kouhiListDTO=" + kouhiListDTO +
				'}';
	}
}
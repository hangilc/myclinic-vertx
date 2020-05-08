package dev.myclinic.vertx.dto;

import java.util.List;

public class VisitFullDTO {
	public VisitDTO visit;
	public List<TextDTO> texts;
	public List<ShinryouFullDTO> shinryouList;
	public List<DrugFullDTO> drugs;
	public List<ConductFullDTO> conducts;

	@Override
	public String toString() {
		return "VisitFullDTO{" +
				"visit=" + visit +
				", texts=" + texts +
				", shinryouList=" + shinryouList +
				", drugs=" + drugs +
				", conducts=" + conducts +
				'}';
	}
}
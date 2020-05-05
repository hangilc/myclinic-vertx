package dev.myclinic.vertx.dto;

import dev.myclinic.vertx.dto.annotation.AutoInc;
import dev.myclinic.vertx.dto.annotation.Primary;

import java.util.Objects;

public class DrugDTO {
	@Primary
	@AutoInc
	public int drugId;
	public int visitId;
	public int iyakuhincode;
	public double amount;
	public String usage;
	public int days;
	public int category;
	public int prescribed;

	public static dev.myclinic.vertx.dto.DrugDTO copy(dev.myclinic.vertx.dto.DrugDTO src){
		dev.myclinic.vertx.dto.DrugDTO dst = new dev.myclinic.vertx.dto.DrugDTO();
		dst.drugId = src.drugId;
		dst.visitId = src.visitId;
		dst.iyakuhincode = src.iyakuhincode;
		dst.amount = src.amount;
		dst.usage = src.usage;
		dst.days = src.days;
		dst.category = src.category;
		dst.prescribed = src.prescribed;
		return dst;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		dev.myclinic.vertx.dto.DrugDTO drugDTO = (dev.myclinic.vertx.dto.DrugDTO) o;
		return drugId == drugDTO.drugId &&
				visitId == drugDTO.visitId &&
				iyakuhincode == drugDTO.iyakuhincode &&
				Double.compare(drugDTO.amount, amount) == 0 &&
				days == drugDTO.days &&
				category == drugDTO.category &&
				prescribed == drugDTO.prescribed &&
				Objects.equals(usage, drugDTO.usage);
	}

	@Override
	public int hashCode() {
		return Objects.hash(drugId, visitId, iyakuhincode, amount, usage, days, category, prescribed);
	}

	@Override
	public String toString() {
		return "DrugDTO{" +
				"drugId=" + drugId +
				", visitId=" + visitId +
				", iyakuhincode=" + iyakuhincode +
				", amount=" + amount +
				", usage='" + usage + '\'' +
				", days=" + days +
				", category=" + category +
				", prescribed=" + prescribed +
				'}';
	}
}
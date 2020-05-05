package dev.myclinic.vertx.dto;

import java.util.List;
import java.util.stream.Collectors;

public class ConductFullDTO {
	public ConductDTO conduct;
	public GazouLabelDTO gazouLabel;
	public List<dev.myclinic.vertx.dto.ConductShinryouFullDTO> conductShinryouList;
	public List<ConductDrugFullDTO> conductDrugs;
	public List<dev.myclinic.vertx.dto.ConductKizaiFullDTO> conductKizaiList;

	public static dev.myclinic.vertx.dto.ConductFullDTO deepCopy(dev.myclinic.vertx.dto.ConductFullDTO src){
		dev.myclinic.vertx.dto.ConductFullDTO dst = new dev.myclinic.vertx.dto.ConductFullDTO();
		dst.conduct = ConductDTO.copy(src.conduct);
		if( src.gazouLabel != null ){
			dst.gazouLabel = GazouLabelDTO.copy(src.gazouLabel);
		}
		dst.conductShinryouList = src.conductShinryouList.stream().map(dev.myclinic.vertx.dto.ConductShinryouFullDTO::copy).collect(Collectors.toList());
		dst.conductDrugs = src.conductDrugs.stream().map(ConductDrugFullDTO::copy).collect(Collectors.toList());
		dst.conductKizaiList = src.conductKizaiList.stream().map(dev.myclinic.vertx.dto.ConductKizaiFullDTO::copy).collect(Collectors.toList());
		return dst;
	}

	@Override
	public String toString() {
		return "ConductFullDTO{" +
				"conduct=" + conduct +
				", gazouLabel=" + gazouLabel +
				", conductShinryouList=" + conductShinryouList +
				", conductDrugs=" + conductDrugs +
				", conductKizaiList=" + conductKizaiList +
				'}';
	}
}
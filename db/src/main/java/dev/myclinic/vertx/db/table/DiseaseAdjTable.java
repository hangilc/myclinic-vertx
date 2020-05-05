package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.DiseaseAdjDTO;


import java.util.List;


public class DiseaseAdjTable extends Table<DiseaseAdjDTO> {

  private static List<Column<DiseaseAdjDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "disease_adj_id",
                "diseaseAdjId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.diseaseAdjId),
                (rs, i, dto) -> dto.diseaseAdjId = rs.getInt(i)),
            new Column<>(
                "disease_id",
                "diseaseId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.diseaseId),
                (rs, i, dto) -> dto.diseaseId = rs.getInt(i)),
            new Column<>(
                "shuushokugocode",
                "shuushokugocode",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shuushokugocode),
                (rs, i, dto) -> dto.shuushokugocode = rs.getInt(i)));
  }

  @Override
  public DiseaseAdjDTO newInstance(){
    return new DiseaseAdjDTO();
  }


  @Override()
  public String getTableName() {
    return "disease_adj";
  }

  @Override()
  protected Class<DiseaseAdjDTO> getClassDTO() {
    return DiseaseAdjDTO.class;
  }

  @Override()
  protected List<Column<DiseaseAdjDTO>> getColumns() {
    return columns;
  }
}

package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ConductShinryouDTO;


import java.util.List;


public class ConductShinryouTable extends Table<ConductShinryouDTO> {

  private static List<Column<ConductShinryouDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "id",
                "conductShinryouId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.conductShinryouId),
                (rs, i, dto) -> dto.conductShinryouId = rs.getInt(i)),
            new Column<>(
                "visit_conduct_id",
                "conductId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.conductId),
                (rs, i, dto) -> dto.conductId = rs.getInt(i)),
            new Column<>(
                "shinryoucode",
                "shinryoucode",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shinryoucode),
                (rs, i, dto) -> dto.shinryoucode = rs.getInt(i)));
  }

  @Override
  public ConductShinryouDTO newInstance(){
    return new ConductShinryouDTO();
  }

  @Override()
  public String getTableName() {
    return "visit_conduct_shinryou";
  }

  @Override()
  protected Class<ConductShinryouDTO> getClassDTO() {
    return ConductShinryouDTO.class;
  }

  @Override()
  protected List<Column<ConductShinryouDTO>> getColumns() {
    return columns;
  }
}

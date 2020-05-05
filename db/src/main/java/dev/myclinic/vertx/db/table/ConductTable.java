package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ConductDTO;


import java.util.List;


public class ConductTable extends Table<ConductDTO> {

  private final static List<Column<ConductDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "id",
                "conductId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.conductId),
                (rs, i, dto) -> dto.conductId = rs.getInt(i)),
            new Column<>(
                "visit_id",
                "visitId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.visitId),
                (rs, i, dto) -> dto.visitId = rs.getInt(i)),
            new Column<>(
                "kind",
                "kind",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.kind),
                (rs, i, dto) -> dto.kind = rs.getInt(i)));
  }

  @Override
  public ConductDTO newInstance(){
    return new ConductDTO();
  }


  @Override()
  public String getTableName() {
    return "visit_conduct";
  }

  @Override()
  protected Class<ConductDTO> getClassDTO() {
    return ConductDTO.class;
  }

  @Override()
  protected List<Column<ConductDTO>> getColumns() {
    return columns;
  }
}

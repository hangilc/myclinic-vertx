package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.WqueueDTO;


import java.util.List;


public class WqueueTable extends Table<WqueueDTO> {

  private static List<Column<WqueueDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "visit_id",
                "visitId",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.visitId),
                (rs, i, dto) -> dto.visitId = rs.getInt(i)),
            new Column<>(
                "wait_state",
                "waitState",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.waitState),
                (rs, i, dto) -> dto.waitState = rs.getInt(i)));
  }

  @Override
  public WqueueDTO newInstance(){
    return new WqueueDTO();
  }


  @Override()
  public String getTableName() {
    return "wqueue";
  }

  @Override()
  protected Class<WqueueDTO> getClassDTO() {
    return WqueueDTO.class;
  }

  @Override()
  protected List<Column<WqueueDTO>> getColumns() {
    return columns;
  }
}

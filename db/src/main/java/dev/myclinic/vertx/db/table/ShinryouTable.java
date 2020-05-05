package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ShinryouDTO;


import java.util.List;


public class ShinryouTable extends Table<ShinryouDTO> {

  private static List<Column<ShinryouDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "shinryou_id",
                "shinryouId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.shinryouId),
                (rs, i, dto) -> dto.shinryouId = rs.getInt(i)),
            new Column<>(
                "visit_id",
                "visitId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.visitId),
                (rs, i, dto) -> dto.visitId = rs.getInt(i)),
            new Column<>(
                "shinryoucode",
                "shinryoucode",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shinryoucode),
                (rs, i, dto) -> dto.shinryoucode = rs.getInt(i)));
  }

  @Override
  public ShinryouDTO newInstance(){
    return new ShinryouDTO();
  }


  @Override()
  public String getTableName() {
    return "visit_shinryou";
  }

  @Override()
  protected Class<ShinryouDTO> getClassDTO() {
    return ShinryouDTO.class;
  }

  @Override()
  protected List<Column<ShinryouDTO>> getColumns() {
    return columns;
  }
}

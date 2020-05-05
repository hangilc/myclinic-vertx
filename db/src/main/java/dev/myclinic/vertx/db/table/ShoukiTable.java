package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ShoukiDTO;


import java.util.List;


public class ShoukiTable extends Table<ShoukiDTO> {

  private static List<Column<ShoukiDTO>> columns;

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
                "shouki",
                "shouki",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.shouki),
                (rs, i, dto) -> dto.shouki = rs.getString(i)));
  }

  @Override
  public ShoukiDTO newInstance(){
    return new ShoukiDTO();
  }


  @Override()
  public String getTableName() {
    return "shouki";
  }

  @Override()
  protected Class<ShoukiDTO> getClassDTO() {
    return ShoukiDTO.class;
  }

  @Override()
  protected List<Column<ShoukiDTO>> getColumns() {
    return columns;
  }
}

package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.GazouLabelDTO;


import java.util.List;


public class GazouLabelTable extends Table<GazouLabelDTO> {

  private static List<Column<GazouLabelDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "visit_conduct_id",
                "conductId",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.conductId),
                (rs, i, dto) -> dto.conductId = rs.getInt(i)),
            new Column<>(
                "label",
                "label",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.label),
                (rs, i, dto) -> dto.label = rs.getString(i)));
  }

  @Override
  public GazouLabelDTO newInstance(){
    return new GazouLabelDTO();
  }


  @Override()
  public String getTableName() {
    return "visit_gazou_label";
  }

  @Override()
  protected Class<GazouLabelDTO> getClassDTO() {
    return GazouLabelDTO.class;
  }

  @Override()
  protected List<Column<GazouLabelDTO>> getColumns() {
    return columns;
  }
}

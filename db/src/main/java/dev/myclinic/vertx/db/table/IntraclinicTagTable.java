package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.IntraclinicTagDTO;


import java.util.List;


public class IntraclinicTagTable extends Table<IntraclinicTagDTO> {

  private static List<Column<IntraclinicTagDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "id",
                "tagId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.tagId),
                (rs, i, dto) -> dto.tagId = rs.getInt(i)),
            new Column<>(
                "name",
                "name",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.name),
                (rs, i, dto) -> dto.name = rs.getString(i)));
  }

  @Override
  public IntraclinicTagDTO newInstance(){
    return new IntraclinicTagDTO();
  }


  @Override()
  public String getTableName() {
    return "intraclinic_tag";
  }

  @Override()
  protected Class<IntraclinicTagDTO> getClassDTO() {
    return IntraclinicTagDTO.class;
  }

  @Override()
  protected List<Column<IntraclinicTagDTO>> getColumns() {
    return columns;
  }
}

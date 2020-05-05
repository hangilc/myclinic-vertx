package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.IntraclinicPostDTO;


import java.time.LocalDate;
import java.util.List;


public class IntraclinicPostTable extends Table<IntraclinicPostDTO> {

  private static List<Column<IntraclinicPostDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "id",
                "id",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.id),
                (rs, i, dto) -> dto.id = rs.getInt(i)),
            new Column<>(
                "content",
                "content",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.content),
                (rs, i, dto) -> dto.content = rs.getString(i)),
            new Column<>(
                "created_at",
                "createdAt",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, LocalDate.parse(dto.createdAt)),
                (rs, i, dto) -> dto.createdAt = rs.getObject(i, LocalDate.class).toString()));
  }

  @Override
  public IntraclinicPostDTO newInstance(){
    return new IntraclinicPostDTO();
  }


  @Override()
  public String getTableName() {
    return "intraclinic_post";
  }

  @Override()
  protected Class<IntraclinicPostDTO> getClassDTO() {
    return IntraclinicPostDTO.class;
  }

  @Override()
  protected List<Column<IntraclinicPostDTO>> getColumns() {
    return columns;
  }
}

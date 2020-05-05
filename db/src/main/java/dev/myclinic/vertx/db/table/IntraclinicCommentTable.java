package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.IntraclinicCommentDTO;


import java.time.LocalDate;
import java.util.List;


public class IntraclinicCommentTable extends Table<IntraclinicCommentDTO> {

  private static List<Column<IntraclinicCommentDTO>> columns;

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
                "name",
                "name",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.name),
                (rs, i, dto) -> dto.name = rs.getString(i)),
            new Column<>(
                "content",
                "content",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.content),
                (rs, i, dto) -> dto.content = rs.getString(i)),
            new Column<>(
                "post_id",
                "postId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.postId),
                (rs, i, dto) -> dto.postId = rs.getInt(i)),
            new Column<>(
                "created_at",
                "createdAt",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, LocalDate.parse(dto.createdAt)),
                (rs, i, dto) -> dto.createdAt = rs.getObject(i, LocalDate.class).toString()));
  }

  @Override
  public IntraclinicCommentDTO newInstance(){
    return new IntraclinicCommentDTO();
  }


  @Override()
  public String getTableName() {
    return "intraclinic_comment";
  }

  @Override()
  protected Class<IntraclinicCommentDTO> getClassDTO() {
    return IntraclinicCommentDTO.class;
  }

  @Override()
  protected List<Column<IntraclinicCommentDTO>> getColumns() {
    return columns;
  }
}

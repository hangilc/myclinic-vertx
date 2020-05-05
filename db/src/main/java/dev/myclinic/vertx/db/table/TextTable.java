package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.TextDTO;


import java.util.List;


public class TextTable extends Table<TextDTO> {

  private static List<Column<TextDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "text_id",
                "textId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.textId),
                (rs, i, dto) -> dto.textId = rs.getInt(i)),
            new Column<>(
                "visit_id",
                "visitId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.visitId),
                (rs, i, dto) -> dto.visitId = rs.getInt(i)),
            new Column<>(
                "content",
                "content",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.content),
                (rs, i, dto) -> dto.content = rs.getString(i)));
  }

  @Override
  public TextDTO newInstance(){
    return new TextDTO();
  }


  @Override()
  public String getTableName() {
    return "visit_text";
  }

  @Override()
  protected Class<TextDTO> getClassDTO() {
    return TextDTO.class;
  }

  @Override()
  protected List<Column<TextDTO>> getColumns() {
    return columns;
  }
}

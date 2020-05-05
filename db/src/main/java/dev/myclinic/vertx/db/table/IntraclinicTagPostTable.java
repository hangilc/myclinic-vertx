package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.IntraclinicTagPostDTO;


import java.util.List;


public class IntraclinicTagPostTable extends Table<IntraclinicTagPostDTO> {

  private static List<Column<IntraclinicTagPostDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "post_id",
                "postId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.postId),
                (rs, i, dto) -> dto.postId = rs.getInt(i)),
            new Column<>(
                "tag_id",
                "tagId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.tagId),
                (rs, i, dto) -> dto.tagId = rs.getInt(i)));
  }

  @Override
  public IntraclinicTagPostDTO newInstance(){
    return new IntraclinicTagPostDTO();
  }


  @Override()
  public String getTableName() {
    return "intraclinic_tag_post";
  }

  @Override()
  protected Class<IntraclinicTagPostDTO> getClassDTO() {
    return IntraclinicTagPostDTO.class;
  }

  @Override()
  protected List<Column<IntraclinicTagPostDTO>> getColumns() {
    return columns;
  }
}

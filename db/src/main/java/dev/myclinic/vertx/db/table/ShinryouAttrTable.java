package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ShinryouAttrDTO;


import java.util.List;


public class ShinryouAttrTable extends Table<ShinryouAttrDTO> {

  private static List<Column<ShinryouAttrDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "shinryou_id",
                "shinryouId",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shinryouId),
                (rs, i, dto) -> dto.shinryouId = rs.getInt(i)),
            new Column<>(
                "tekiyou",
                "tekiyou",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.tekiyou),
                (rs, i, dto) -> dto.tekiyou = rs.getString(i)));
  }

  @Override
  public ShinryouAttrDTO newInstance(){
    return new ShinryouAttrDTO();
  }


  @Override()
  public String getTableName() {
    return "shinryou_attr";
  }

  @Override()
  protected Class<ShinryouAttrDTO> getClassDTO() {
    return ShinryouAttrDTO.class;
  }

  @Override()
  protected List<Column<ShinryouAttrDTO>> getColumns() {
    return columns;
  }
}

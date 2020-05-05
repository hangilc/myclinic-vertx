package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ShuushokugoMasterDTO;


import java.util.List;


public class ShuushokugoMasterTable extends Table<ShuushokugoMasterDTO> {

  private static List<Column<ShuushokugoMasterDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "shuushokugocode",
                "shuushokugocode",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shuushokugocode),
                (rs, i, dto) -> dto.shuushokugocode = rs.getInt(i)),
            new Column<>(
                "name",
                "name",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.name),
                (rs, i, dto) -> dto.name = rs.getString(i)));
  }

  @Override
  public ShuushokugoMasterDTO newInstance(){
    return new ShuushokugoMasterDTO();
  }


  @Override()
  public String getTableName() {
    return "shuushokugo_master";
  }

  @Override()
  protected Class<ShuushokugoMasterDTO> getClassDTO() {
    return ShuushokugoMasterDTO.class;
  }

  @Override()
  protected List<Column<ShuushokugoMasterDTO>> getColumns() {
    return columns;
  }
}

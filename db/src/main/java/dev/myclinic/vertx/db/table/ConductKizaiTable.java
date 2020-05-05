package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ConductKizaiDTO;


import java.util.List;


public class ConductKizaiTable extends Table<ConductKizaiDTO> {

  private static List<Column<ConductKizaiDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "id",
                "conductKizaiId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.conductKizaiId),
                (rs, i, dto) -> dto.conductKizaiId = rs.getInt(i)),
            new Column<>(
                "visit_conduct_id",
                "conductId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.conductId),
                (rs, i, dto) -> dto.conductId = rs.getInt(i)),
            new Column<>(
                "kizaicode",
                "kizaicode",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.kizaicode),
                (rs, i, dto) -> dto.kizaicode = rs.getInt(i)),
            new Column<>(
                "amount",
                "amount",
                false,
                false,
                (stmt, i, dto) -> stmt.setDouble(i, dto.amount),
                (rs, i, dto) -> dto.amount = rs.getDouble(i)));
  }

  @Override
  public ConductKizaiDTO newInstance() {
    return new ConductKizaiDTO();
  }

  @Override()
  public String getTableName() {
    return "visit_conduct_kizai";
  }

  @Override()
  protected Class<ConductKizaiDTO> getClassDTO() {
    return ConductKizaiDTO.class;
  }

  @Override()
  protected List<Column<ConductKizaiDTO>> getColumns() {
    return columns;
  }
}

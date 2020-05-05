package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.PharmaDrugDTO;


import java.util.List;


public class PharmaDrugTable extends Table<PharmaDrugDTO> {

  private static List<Column<PharmaDrugDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "iyakuhincode",
                "iyakuhincode",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.iyakuhincode),
                (rs, i, dto) -> dto.iyakuhincode = rs.getInt(i)),
            new Column<>(
                "description",
                "description",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.description),
                (rs, i, dto) -> dto.description = rs.getString(i)),
            new Column<>(
                "sideeffect",
                "sideeffect",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.sideeffect),
                (rs, i, dto) -> dto.sideeffect = rs.getString(i)));
  }

  @Override
  public PharmaDrugDTO newInstance(){
    return new PharmaDrugDTO();
  }


  @Override()
  public String getTableName() {
    return "pharma_drug";
  }

  @Override()
  protected Class<PharmaDrugDTO> getClassDTO() {
    return PharmaDrugDTO.class;
  }

  @Override()
  protected List<Column<PharmaDrugDTO>> getColumns() {
    return columns;
  }
}

package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.DrugAttrDTO;


import java.util.List;


public class DrugAttrTable extends Table<DrugAttrDTO> {

  private static List<Column<DrugAttrDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "drug_id",
                "drugId",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.drugId),
                (rs, i, dto) -> dto.drugId = rs.getInt(i)),
            new Column<>(
                "tekiyou",
                "tekiyou",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.tekiyou),
                (rs, i, dto) -> dto.tekiyou = rs.getString(i)));
  }

  @Override
  public DrugAttrDTO newInstance(){
    return new DrugAttrDTO();
  }


  @Override()
  public String getTableName() {
    return "drug_attr";
  }

  @Override()
  protected Class<DrugAttrDTO> getClassDTO() {
    return DrugAttrDTO.class;
  }

  @Override()
  protected List<Column<DrugAttrDTO>> getColumns() {
    return columns;
  }
}

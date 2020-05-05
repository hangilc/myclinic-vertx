package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ShahokokuhoDTO;


import java.time.LocalDate;
import java.util.List;


public class ShahokokuhoTable extends Table<ShahokokuhoDTO> {

  private static List<Column<ShahokokuhoDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "shahokokuho_id",
                "shahokokuhoId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.shahokokuhoId),
                (rs, i, dto) -> dto.shahokokuhoId = rs.getInt(i)),
            new Column<>(
                "patient_id",
                "patientId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.patientId),
                (rs, i, dto) -> dto.patientId = rs.getInt(i)),
            new Column<>(
                "hokensha_bangou",
                "hokenshaBangou",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.hokenshaBangou),
                (rs, i, dto) -> dto.hokenshaBangou = rs.getInt(i)),
            new Column<>(
                "hihokensha_kigou",
                "hihokenshaKigou",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.hihokenshaKigou),
                (rs, i, dto) -> dto.hihokenshaKigou = rs.getString(i)),
            new Column<>(
                "hihokensha_bangou",
                "hihokenshaBangou",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.hihokenshaBangou),
                (rs, i, dto) -> dto.hihokenshaBangou = rs.getString(i)),
            new Column<>(
                "honnin",
                "honnin",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.honnin),
                (rs, i, dto) -> dto.honnin = rs.getInt(i)),
            new Column<>(
                "valid_from",
                "validFrom",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, LocalDate.parse(dto.validFrom)),
                (rs, i, dto) -> dto.validFrom = rs.getObject(i, LocalDate.class).toString()),
            new Column<>(
                "valid_upto",
                "validUpto",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, dto.validUpto),
                (rs, i, dto) -> dto.validUpto = rs.getString(i)),
            new Column<>(
                "kourei",
                "kourei",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.kourei),
                (rs, i, dto) -> dto.kourei = rs.getInt(i)));
  }

  @Override
  public ShahokokuhoDTO newInstance(){
    return new ShahokokuhoDTO();
  }


  @Override()
  public String getTableName() {
    return "hoken_shahokokuho";
  }

  @Override()
  protected Class<ShahokokuhoDTO> getClassDTO() {
    return ShahokokuhoDTO.class;
  }

  @Override()
  protected List<Column<ShahokokuhoDTO>> getColumns() {
    return columns;
  }
}

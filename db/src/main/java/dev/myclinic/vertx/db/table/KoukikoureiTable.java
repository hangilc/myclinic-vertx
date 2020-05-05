package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.KoukikoureiDTO;


import java.time.LocalDate;
import java.util.List;


public class KoukikoureiTable extends Table<KoukikoureiDTO> {

  private static List<Column<KoukikoureiDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "koukikourei_id",
                "koukikoureiId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.koukikoureiId),
                (rs, i, dto) -> dto.koukikoureiId = rs.getInt(i)),
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
                (stmt, i, dto) -> stmt.setString(i, dto.hokenshaBangou),
                (rs, i, dto) -> dto.hokenshaBangou = rs.getString(i)),
            new Column<>(
                "hihokensha_bangou",
                "hihokenshaBangou",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.hihokenshaBangou),
                (rs, i, dto) -> dto.hihokenshaBangou = rs.getString(i)),
            new Column<>(
                "futan_wari",
                "futanWari",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.futanWari),
                (rs, i, dto) -> dto.futanWari = rs.getInt(i)),
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
                (rs, i, dto) -> dto.validUpto = rs.getString(i)));
  }

  @Override
  public KoukikoureiDTO newInstance(){
    return new KoukikoureiDTO();
  }


  @Override()
  public String getTableName() {
    return "hoken_koukikourei";
  }

  @Override()
  protected Class<KoukikoureiDTO> getClassDTO() {
    return KoukikoureiDTO.class;
  }

  @Override()
  protected List<Column<KoukikoureiDTO>> getColumns() {
    return columns;
  }
}

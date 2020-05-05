package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.KouhiDTO;


import java.time.LocalDate;
import java.util.List;


public class KouhiTable extends Table<KouhiDTO> {

  private static List<Column<KouhiDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "kouhi_id",
                "kouhiId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.kouhiId),
                (rs, i, dto) -> dto.kouhiId = rs.getInt(i)),
            new Column<>(
                "futansha",
                "futansha",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.futansha),
                (rs, i, dto) -> dto.futansha = rs.getInt(i)),
            new Column<>(
                "jukyuusha",
                "jukyuusha",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.jukyuusha),
                (rs, i, dto) -> dto.jukyuusha = rs.getInt(i)),
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
                "patient_id",
                "patientId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.patientId),
                (rs, i, dto) -> dto.patientId = rs.getInt(i)));
  }

  @Override
  public KouhiDTO newInstance(){
    return new KouhiDTO();
  }


  @Override()
  public String getTableName() {
    return "kouhi";
  }

  @Override()
  protected Class<KouhiDTO> getClassDTO() {
    return KouhiDTO.class;
  }

  @Override()
  protected List<Column<KouhiDTO>> getColumns() {
    return columns;
  }
}

package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.RoujinDTO;


import java.time.LocalDate;
import java.util.List;


public class RoujinTable extends Table<RoujinDTO> {

  private static List<Column<RoujinDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "roujin_id",
                "roujinId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.roujinId),
                (rs, i, dto) -> dto.roujinId = rs.getInt(i)),
            new Column<>(
                "patient_id",
                "patientId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.patientId),
                (rs, i, dto) -> dto.patientId = rs.getInt(i)),
            new Column<>(
                "shichouson",
                "shichouson",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shichouson),
                (rs, i, dto) -> dto.shichouson = rs.getInt(i)),
            new Column<>(
                "jukyuusha",
                "jukyuusha",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.jukyuusha),
                (rs, i, dto) -> dto.jukyuusha = rs.getInt(i)),
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
  public RoujinDTO newInstance(){
    return new RoujinDTO();
  }


  @Override()
  public String getTableName() {
    return "hoken_roujin";
  }

  @Override()
  protected Class<RoujinDTO> getClassDTO() {
    return RoujinDTO.class;
  }

  @Override()
  protected List<Column<RoujinDTO>> getColumns() {
    return columns;
  }
}

package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.db.TableHelper;
import dev.myclinic.vertx.dto.VisitDTO;


import java.time.LocalDateTime;
import java.util.List;


public class VisitTable extends Table<VisitDTO> {

  private static List<Column<VisitDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "visit_id",
                "visitId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.visitId),
                (rs, i, dto) -> dto.visitId = rs.getInt(i)),
            new Column<>(
                "patient_id",
                "patientId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.patientId),
                (rs, i, dto) -> dto.patientId = rs.getInt(i)),
            new Column<>(
                "v_datetime",
                "visitedAt",
                false,
                false,
                (stmt, i, dto) ->
                    stmt.setObject(i, TableHelper.stringToLocalDateTime(dto.visitedAt)),
                (rs, i, dto) ->
                    dto.visitedAt =
                        TableHelper.localDateTimeToString(
                            rs.getObject(i, LocalDateTime.class))),
            new Column<>(
                "shahokokuho_id",
                "shahokokuhoId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shahokokuhoId),
                (rs, i, dto) -> dto.shahokokuhoId = rs.getInt(i)),
            new Column<>(
                "roujin_id",
                "roujinId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.roujinId),
                (rs, i, dto) -> dto.roujinId = rs.getInt(i)),
            new Column<>(
                "kouhi_1_id",
                "kouhi1Id",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.kouhi1Id),
                (rs, i, dto) -> dto.kouhi1Id = rs.getInt(i)),
            new Column<>(
                "kouhi_2_id",
                "kouhi2Id",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.kouhi2Id),
                (rs, i, dto) -> dto.kouhi2Id = rs.getInt(i)),
            new Column<>(
                "kouhi_3_id",
                "kouhi3Id",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.kouhi3Id),
                (rs, i, dto) -> dto.kouhi3Id = rs.getInt(i)),
            new Column<>(
                "koukikourei_id",
                "koukikoureiId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.koukikoureiId),
                (rs, i, dto) -> dto.koukikoureiId = rs.getInt(i)));
  }

  @Override
  public VisitDTO newInstance(){
    return new VisitDTO();
  }


  @Override()
  public String getTableName() {
    return "visit";
  }

  @Override()
  protected Class<VisitDTO> getClassDTO() {
    return VisitDTO.class;
  }

  @Override()
  protected List<Column<VisitDTO>> getColumns() {
    return columns;
  }
}

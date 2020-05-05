package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.db.TableHelper;
import dev.myclinic.vertx.dto.PracticeLogDTO;


import java.time.LocalDateTime;
import java.util.List;


public class PracticeLogTable extends Table<PracticeLogDTO> {

  private static List<Column<PracticeLogDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "practice_log_id",
                "serialId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.serialId),
                (rs, i, dto) -> dto.serialId = rs.getInt(i)),
            new Column<>(
                "created_at",
                "createdAt",
                false,
                false,
                (stmt, i, dto) ->
                    stmt.setObject(i, TableHelper.stringToLocalDateTime(dto.createdAt)),
                (rs, i, dto) ->
                    dto.createdAt =
                        TableHelper.localDateTimeToString(
                            rs.getObject(i, LocalDateTime.class))),
            new Column<>(
                "kind",
                "kind",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.kind),
                (rs, i, dto) -> dto.kind = rs.getString(i)),
            new Column<>(
                "body",
                "body",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.body),
                (rs, i, dto) -> dto.body = rs.getString(i)));
  }

  @Override
  public PracticeLogDTO newInstance(){
    return new PracticeLogDTO();
  }


  @Override()
  public String getTableName() {
    return "practice_log";
  }

  @Override()
  protected Class<PracticeLogDTO> getClassDTO() {
    return PracticeLogDTO.class;
  }

  @Override()
  protected List<Column<PracticeLogDTO>> getColumns() {
    return columns;
  }
}

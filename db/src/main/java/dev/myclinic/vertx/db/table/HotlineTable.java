package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.db.TableHelper;
import dev.myclinic.vertx.dto.HotlineDTO;


import java.time.LocalDateTime;
import java.util.List;


public class HotlineTable extends Table<HotlineDTO> {

  private static List<Column<HotlineDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "hotline_id",
                "hotlineId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.hotlineId),
                (rs, i, dto) -> dto.hotlineId = rs.getInt(i)),
            new Column<>(
                "message",
                "message",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.message),
                (rs, i, dto) -> dto.message = rs.getString(i)),
            new Column<>(
                "sender",
                "sender",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.sender),
                (rs, i, dto) -> dto.sender = rs.getString(i)),
            new Column<>(
                "recipient",
                "recipient",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.recipient),
                (rs, i, dto) -> dto.recipient = rs.getString(i)),
            new Column<>(
                "m_datetime",
                "postedAt",
                false,
                false,
                (stmt, i, dto) ->
                    stmt.setObject(i, TableHelper.stringToLocalDateTime(dto.postedAt)),
                (rs, i, dto) ->
                    dto.postedAt =
                        TableHelper.localDateTimeToString(
                            rs.getObject(i, LocalDateTime.class))));
  }

  @Override
  public HotlineDTO newInstance(){
    return new HotlineDTO();
  }


  @Override()
  public String getTableName() {
    return "hotline";
  }

  @Override()
  protected Class<HotlineDTO> getClassDTO() {
    return HotlineDTO.class;
  }

  @Override()
  protected List<Column<HotlineDTO>> getColumns() {
    return columns;
  }
}

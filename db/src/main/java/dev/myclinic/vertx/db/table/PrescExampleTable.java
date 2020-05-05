package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.PrescExampleDTO;


import java.time.LocalDate;
import java.util.List;


public class PrescExampleTable extends Table<PrescExampleDTO> {

  private static List<Column<PrescExampleDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "presc_example_id",
                "prescExampleId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.prescExampleId),
                (rs, i, dto) -> dto.prescExampleId = rs.getInt(i)),
            new Column<>(
                "m_iyakuhincode",
                "iyakuhincode",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.iyakuhincode),
                (rs, i, dto) -> dto.iyakuhincode = rs.getInt(i)),
            new Column<>(
                "m_master_valid_from",
                "masterValidFrom",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, LocalDate.parse(dto.masterValidFrom)),
                (rs, i, dto) -> dto.masterValidFrom = rs.getObject(i, LocalDate.class).toString()),
            new Column<>(
                "m_amount",
                "amount",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.amount),
                (rs, i, dto) -> dto.amount = rs.getString(i)),
            new Column<>(
                "m_usage",
                "usage",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.usage),
                (rs, i, dto) -> dto.usage = rs.getString(i)),
            new Column<>(
                "m_days",
                "days",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.days),
                (rs, i, dto) -> dto.days = rs.getInt(i)),
            new Column<>(
                "m_category",
                "category",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.category),
                (rs, i, dto) -> dto.category = rs.getInt(i)),
            new Column<>(
                "m_comment",
                "comment",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.comment),
                (rs, i, dto) -> dto.comment = rs.getString(i)));
  }

  @Override
  public PrescExampleDTO newInstance(){
    return new PrescExampleDTO();
  }


  @Override()
  public String getTableName() {
    return "presc_example";
  }

  @Override()
  protected Class<PrescExampleDTO> getClassDTO() {
    return PrescExampleDTO.class;
  }

  @Override()
  protected List<Column<PrescExampleDTO>> getColumns() {
    return columns;
  }
}

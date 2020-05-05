package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.db.TableHelper;
import dev.myclinic.vertx.dto.PaymentDTO;


import java.time.LocalDateTime;
import java.util.List;


public class PaymentTable extends Table<PaymentDTO> {

  private static List<Column<PaymentDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "visit_id",
                "visitId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.visitId),
                (rs, i, dto) -> dto.visitId = rs.getInt(i)),
            new Column<>(
                "amount",
                "amount",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.amount),
                (rs, i, dto) -> dto.amount = rs.getInt(i)),
            new Column<>(
                "paytime",
                "paytime",
                false,
                false,
                (stmt, i, dto) ->
                    stmt.setObject(i, TableHelper.stringToLocalDateTime(dto.paytime)),
                (rs, i, dto) ->
                    dto.paytime =
                        TableHelper.localDateTimeToString(
                            rs.getObject(i, LocalDateTime.class))));
  }

  @Override
  public PaymentDTO newInstance(){
    return new PaymentDTO();
  }


  @Override()
  public String getTableName() {
    return "visit_payment";
  }

  @Override()
  protected Class<PaymentDTO> getClassDTO() {
    return PaymentDTO.class;
  }

  @Override()
  protected List<Column<PaymentDTO>> getColumns() {
    return columns;
  }
}

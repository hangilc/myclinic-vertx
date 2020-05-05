package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.PharmaQueueDTO;


import java.util.List;


public class PharmaQueueTable extends Table<PharmaQueueDTO> {

  private static List<Column<PharmaQueueDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "visit_id",
                "visitId",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.visitId),
                (rs, i, dto) -> dto.visitId = rs.getInt(i)),
            new Column<>(
                "pharma_state",
                "pharmaState",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.pharmaState),
                (rs, i, dto) -> dto.pharmaState = rs.getInt(i)));
  }

  @Override
  public PharmaQueueDTO newInstance(){
    return new PharmaQueueDTO();
  }


  @Override()
  public String getTableName() {
    return "pharma_queue";
  }

  @Override()
  protected Class<PharmaQueueDTO> getClassDTO() {
    return PharmaQueueDTO.class;
  }

  @Override()
  protected List<Column<PharmaQueueDTO>> getColumns() {
    return columns;
  }
}

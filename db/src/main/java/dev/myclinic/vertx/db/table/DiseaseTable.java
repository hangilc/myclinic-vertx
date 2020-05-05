package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.DiseaseDTO;


import java.time.LocalDate;
import java.util.List;


public class DiseaseTable extends Table<DiseaseDTO> {

  private static List<Column<DiseaseDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "disease_id",
                "diseaseId",
                true,
                true,
                (stmt, i, dto) -> stmt.setInt(i, dto.diseaseId),
                (rs, i, dto) -> dto.diseaseId = rs.getInt(i)),
            new Column<>(
                "patient_id",
                "patientId",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.patientId),
                (rs, i, dto) -> dto.patientId = rs.getInt(i)),
            new Column<>(
                "shoubyoumeicode",
                "shoubyoumeicode",
                false,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shoubyoumeicode),
                (rs, i, dto) -> dto.shoubyoumeicode = rs.getInt(i)),
            new Column<>(
                "start_date",
                "startDate",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, LocalDate.parse(dto.startDate)),
                (rs, i, dto) -> dto.startDate = rs.getObject(i, LocalDate.class).toString()),
            new Column<>(
                "end_date",
                "endDate",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, dto.endDate),
                (rs, i, dto) -> dto.endDate = rs.getString(i)),
            new Column<>(
                "end_reason",
                "endReason",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.endReason)),
                (rs, i, dto) -> dto.endReason = rs.getString(i).charAt(0)));
  }

  @Override
  public DiseaseDTO newInstance(){
    return new DiseaseDTO();
  }


  @Override()
  public String getTableName() {
    return "disease";
  }

  @Override()
  protected Class<DiseaseDTO> getClassDTO() {
    return DiseaseDTO.class;
  }

  @Override()
  protected List<Column<DiseaseDTO>> getColumns() {
    return columns;
  }
}

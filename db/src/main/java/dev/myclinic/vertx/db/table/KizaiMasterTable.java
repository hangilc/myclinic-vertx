package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.KizaiMasterDTO;


import java.time.LocalDate;
import java.util.List;


public class KizaiMasterTable extends Table<KizaiMasterDTO> {

  private static List<Column<KizaiMasterDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "kizaicode",
                "kizaicode",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.kizaicode),
                (rs, i, dto) -> dto.kizaicode = rs.getInt(i)),
            new Column<>(
                "name",
                "name",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.name),
                (rs, i, dto) -> dto.name = rs.getString(i)),
            new Column<>(
                "yomi",
                "yomi",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.yomi),
                (rs, i, dto) -> dto.yomi = rs.getString(i)),
            new Column<>(
                "unit",
                "unit",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.unit),
                (rs, i, dto) -> dto.unit = rs.getString(i)),
            new Column<>(
                "kingaku",
                "kingaku",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.kingaku)),
                (rs, i, dto) -> dto.kingaku = Double.parseDouble(rs.getString(i))),
            new Column<>(
                "valid_from",
                "validFrom",
                true,
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
  public KizaiMasterDTO newInstance(){
    return new KizaiMasterDTO();
  }


  @Override()
  public String getTableName() {
    return "tokuteikizai_master_arch";
  }

  @Override()
  protected Class<KizaiMasterDTO> getClassDTO() {
    return KizaiMasterDTO.class;
  }

  @Override()
  protected List<Column<KizaiMasterDTO>> getColumns() {
    return columns;
  }
}

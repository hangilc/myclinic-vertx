package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.IyakuhinMasterDTO;


import java.time.LocalDate;
import java.util.List;


public class IyakuhinMasterTable extends Table<IyakuhinMasterDTO> {

  private static List<Column<IyakuhinMasterDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "iyakuhincode",
                "iyakuhincode",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.iyakuhincode),
                (rs, i, dto) -> dto.iyakuhincode = rs.getInt(i)),
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
                "yakka",
                "yakka",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.yakka)),
                (rs, i, dto) -> dto.yakka = Double.parseDouble(rs.getString(i))),
            new Column<>(
                "madoku",
                "madoku",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.madoku)),
                (rs, i, dto) -> dto.madoku = rs.getString(i).charAt(0)),
            new Column<>(
                "kouhatsu",
                "kouhatsu",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.kouhatsu)),
                (rs, i, dto) -> dto.kouhatsu = rs.getString(i).charAt(0)),
            new Column<>(
                "zaikei",
                "zaikei",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.zaikei)),
                (rs, i, dto) -> dto.zaikei = rs.getString(i).charAt(0)),
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
  public IyakuhinMasterDTO newInstance(){
    return new IyakuhinMasterDTO();
  }


  @Override()
  public String getTableName() {
    return "iyakuhin_master_arch";
  }

  @Override()
  protected Class<IyakuhinMasterDTO> getClassDTO() {
    return IyakuhinMasterDTO.class;
  }

  @Override()
  protected List<Column<IyakuhinMasterDTO>> getColumns() {
    return columns;
  }
}

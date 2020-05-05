package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.db.TableHelper;
import dev.myclinic.vertx.dto.ShinryouMasterDTO;


import java.time.LocalDate;
import java.util.List;


public class ShinryouMasterTable extends Table<ShinryouMasterDTO> {

  private static List<Column<ShinryouMasterDTO>> columns;

  static {
    columns =
        List.of(
            new Column<>(
                "shinryoucode",
                "shinryoucode",
                true,
                false,
                (stmt, i, dto) -> stmt.setInt(i, dto.shinryoucode),
                (rs, i, dto) -> dto.shinryoucode = rs.getInt(i)),
            new Column<>(
                "name",
                "name",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.name),
                (rs, i, dto) -> dto.name = rs.getString(i)),
            new Column<>(
                "tensuu",
                "tensuu",
                false,
                false,
                (stmt, i, dto) -> stmt.setObject(i, String.valueOf(dto.tensuu)),
                (rs, i, dto) -> dto.tensuu = TableHelper.tensuuToInteger(rs.getString(i))),
            new Column<>(
                "tensuu_shikibetsu",
                "tensuuShikibetsu",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.tensuuShikibetsu)),
                (rs, i, dto) -> dto.tensuuShikibetsu = rs.getString(i).charAt(0)),
            new Column<>(
                "shuukeisaki",
                "shuukeisaki",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.shuukeisaki),
                (rs, i, dto) -> dto.shuukeisaki = rs.getString(i)),
            new Column<>(
                "houkatsukensa",
                "houkatsukensa",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.houkatsukensa),
                (rs, i, dto) -> dto.houkatsukensa = rs.getString(i)),
            new Column<>(
                "oushinkubun",
                "oushinkubun",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, String.valueOf(dto.oushinkubun)),
                (rs, i, dto) -> dto.oushinkubun = rs.getString(i).charAt(0)),
            new Column<>(
                "kensagroup",
                "kensaGroup",
                false,
                false,
                (stmt, i, dto) -> stmt.setString(i, dto.kensaGroup),
                (rs, i, dto) -> dto.kensaGroup = rs.getString(i)),
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
  public ShinryouMasterDTO newInstance(){
    return new ShinryouMasterDTO();
  }


  @Override()
  public String getTableName() {
    return "shinryoukoui_master_arch";
  }

  @Override()
  protected Class<ShinryouMasterDTO> getClassDTO() {
    return ShinryouMasterDTO.class;
  }

  @Override()
  protected List<Column<ShinryouMasterDTO>> getColumns() {
    return columns;
  }
}

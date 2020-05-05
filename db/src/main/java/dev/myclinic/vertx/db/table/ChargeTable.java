package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ChargeDTO;


import java.util.List;


public class ChargeTable extends Table<ChargeDTO> {

    private final static List<Column<ChargeDTO>> columns;

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
                                "charge",
                                "charge",
                                false,
                                false,
                                (stmt, i, dto) -> stmt.setInt(i, dto.charge),
                                (rs, i, dto) -> dto.charge = rs.getInt(i)));
    }

    @Override()
    public String getTableName() {
        return "visit_charge";
    }

    @Override()
    protected Class<ChargeDTO> getClassDTO() {
        return ChargeDTO.class;
    }

    @Override()
    protected List<Column<ChargeDTO>> getColumns() {
        return columns;
    }

    @Override
    public ChargeDTO newInstance() {
        return new ChargeDTO();
    }

}

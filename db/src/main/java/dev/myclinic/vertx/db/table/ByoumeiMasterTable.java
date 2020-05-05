package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.ByoumeiMasterDTO;


import java.time.LocalDate;
import java.util.List;


public class ByoumeiMasterTable extends Table<ByoumeiMasterDTO> {

    private final static List<Column<ByoumeiMasterDTO>> columns;

    static {
        columns =
                List.of(
                        new Column<>(
                                "shoubyoumeicode",
                                "shoubyoumeicode",
                                true,
                                false,
                                (stmt, i, dto) -> stmt.setInt(i, dto.shoubyoumeicode),
                                (rs, i, dto) -> dto.shoubyoumeicode = rs.getInt(i)),
                        new Column<>(
                                "name",
                                "name",
                                false,
                                false,
                                (stmt, i, dto) -> stmt.setString(i, dto.name),
                                (rs, i, dto) -> dto.name = rs.getString(i)),
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

    @Override()
    public String getTableName() {
        return "shoubyoumei_master_arch";
    }

    @Override
    public ByoumeiMasterDTO newInstance() {
        return new ByoumeiMasterDTO();
    }

    @Override()
    protected Class<ByoumeiMasterDTO> getClassDTO() {
        return ByoumeiMasterDTO.class;
    }

    @Override()
    protected List<Column<ByoumeiMasterDTO>> getColumns() {
        return columns;
    }
}

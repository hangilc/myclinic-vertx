package dev.myclinic.vertx.db.table;

import dev.myclinic.vertx.db.Column;
import dev.myclinic.vertx.db.Table;
import dev.myclinic.vertx.dto.EdabanDTO;

import java.util.List;

public class EdabanTable extends Table<EdabanDTO> {

    private static List<Column<EdabanDTO>> columns;

    static {
        columns = List.of(
                new Column<>(
                        "shahokokuho_id",
                        "shahokokuhoId",
                        true,
                        false,
                        (stmt, i, dto) -> stmt.setInt(i, dto.shahokokuhoId),
                        (rs, i, dto) -> dto.shahokokuhoId = rs.getInt(i)),
                new Column<>(
                        "edaban",
                        "edaban",
                        false,
                        false,
                        (stmt, i, dto) -> stmt.setString(i, dto.edaban),
                        (rs, i, dto) -> dto.edaban = rs.getString(i))
        );
    }

    @Override
    public String getTableName() {
        return "edaban";
    }

    @Override
    public EdabanDTO newInstance() {
        return new EdabanDTO();
    }

    @Override
    protected Class<EdabanDTO> getClassDTO() {
        return EdabanDTO.class;
    }

    @Override
    protected List<Column<EdabanDTO>> getColumns() {
        return columns;
    }
}

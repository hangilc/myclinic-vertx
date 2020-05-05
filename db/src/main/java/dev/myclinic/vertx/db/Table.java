package dev.myclinic.vertx.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.myclinic.vertx.db.Query.*;
import static java.util.stream.Collectors.*;

public abstract class Table<DTO> implements TableInterface<DTO> {

    @Override
    public abstract String getTableName();

    protected abstract Class<DTO> getClassDTO();

    protected abstract List<Column<DTO>> getColumns();

    @Override
    public List<String> getColumnNames(){
        return getColumns().stream().map(Column::getDbColumnName).collect(toList());
    }

    @Override
    public void insert(Query query, DTO dto) {
        Map<Boolean, List<Column<DTO>>> colmap = getColumns().stream().collect(groupingBy(Column::isAutoIncrement));
        if (colmap.get(true) == null || colmap.get(true).size() == 0) {
            String sql = String.format("insert into %s (%s) values (%s)",
                    getTableName(),
                    colmap.get(false).stream().map(Column::getDbColumnName).collect(joining(",")),
                    colmap.get(false).stream().map(Column::getPlaceHolder).collect(joining(","))
            );
            SqlConsumer<PreparedStatement> setter = stmt -> {
                int i = 1;
                for (Column<DTO> c : colmap.get(false)) {
                    c.getFromDTO().set(stmt, i++, dto);
                }
            };
            query.exec(sql, setter);
        } else {
            String sql = String.format("insert into %s (%s) values (%s)",
                    getTableName(),
                    colmap.get(false).stream().map(Column::getDbColumnName).collect(joining(",")),
                    colmap.get(false).stream().map(Column::getPlaceHolder).collect(joining(","))
            );
            List<Column<DTO>> autoIncs = colmap.get(true);
            SqlConsumer<PreparedStatement> setter = stmt -> {
                int i = 1;
                for (Column<DTO> c : colmap.get(false)) {
                    c.getFromDTO().set(stmt, i++, dto);
                }
            };
            SqlConsumer<ResultSet> mapper = rs -> {
                int i = 1;
                for (Column<DTO> c : autoIncs) {
                    c.putIntoDTO().getFromResultSet(rs, i++, dto);
                }
            };
            int n = query.update(sql, PreparedStatement.RETURN_GENERATED_KEYS,
                    mapper, setter);
            if (n != 1) {
                throw new RuntimeException("insert affected non-signle row: " + n);
            }
        }
    }

    @Override
    public void batchCopy(Query query, List<DTO> items){
        if( items.size() == 0 ){
            return;
        }
        List<Column<DTO>> columns = getColumns();
        String sql = String.format("insert into %s (%s) values (%s)",
                getTableName(),
                columns.stream().map(Column::getDbColumnName).collect(joining(",")),
                columns.stream().map(Column::getPlaceHolder).collect(joining(","))
        );
        SqlBiConsumer<PreparedStatement, DTO> setter = (stmt, dto) -> {
            int i = 1;
            for (Column<DTO> c : columns) {
                c.getFromDTO().set(stmt, i++, dto);
            }
        };
        query.batchCopy(sql, setter, items);
    }

    @Override
    public DTO getById(Query query, Object id) {
        List<Column<DTO>> primaries = getColumns().stream().filter(Column::isPrimary).collect(toList());
        if (primaries.size() != 1) {
            throw new RuntimeException("Number of primary key is not one.");
        }
        Column<DTO> primary = primaries.get(0);
        String sql = String.format("select * from %s where %s = ? ",
                getTableName(), primary.getDbColumnName());
        return query.get(sql, this, id);
    }

    @Override
    public void update(Query query, DTO dto) {
        Map<Boolean, List<Column<DTO>>> colmap = getColumns().stream().collect(groupingBy(Column::isPrimary));
        List<Column<DTO>> primaries = colmap.get(true);
        List<Column<DTO>> nonPrimaries = colmap.get(false);
        if (primaries.size() == 0) {
            throw new RuntimeException("No primary keys.");
        }
        String sql = String.format("update %s set %s where %s",
                getTableName(),
                nonPrimaries.stream().map(c -> c.getDbColumnName() + "=" + c.getPlaceHolder()).collect(joining(",")),
                primaries.stream().map(c -> c.getDbColumnName() + "=" + c.getPlaceHolder()).collect(joining(" and "))
        );
        SqlConsumer<PreparedStatement> setter = stmt -> {
            int index = 1;
            for (Column<DTO> c : nonPrimaries) {
                c.getFromDTO().set(stmt, index++, dto);
            }
            for (Column<DTO> c : primaries) {
                c.getFromDTO().set(stmt, index++, dto);
            }
        };
        query.update(sql, setter);
    }

    @Override
    public void delete(Query query, Object id) {
        List<Column<DTO>> primaries = getColumns().stream().filter(Column::isPrimary).collect(toList());
        if (primaries.size() != 1) {
            throw new RuntimeException("Not table with single primary key.");
        }
        Column<DTO> primary = primaries.get(0);
        String sql = String.format("delete from %s where %s = ?",
                getTableName(),
                primary.getDbColumnName()
        );
        SqlConsumer<PreparedStatement> setter = stmt -> {
            stmt.setObject(1, id);
        };
        query.update(sql, setter);
    }

    @Override
    public DTO project(ResultSet rs, Query.ResultSetContext ctx) throws SQLException {
        DTO dto = newInstance();
        for (Column<DTO> c : getColumns()) {
            c.putIntoDTO().getFromResultSet(rs, ctx.nextIndex(), dto);
        }
        return dto;
    }

    private final String dtoName = getClassDTO().getSimpleName().replaceAll("DTO$", "");

    @Override
    public String getDtoName() {
        return dtoName;
    }

    @Override
    public String getDbTableName() {
        return getTableName();
    }

    private final Map<String, String> dtoFieldToDbColumnMap = new HashMap<>();
    {
        for(Column<DTO> c: getColumns()){
            dtoFieldToDbColumnMap.put(c.getDtoFieldName(), c.getDbColumnName());
        }
    }

    @Override
    public Map<String, String> getDtoFieldToDbColumnMap() {
        return dtoFieldToDbColumnMap;
    }
}

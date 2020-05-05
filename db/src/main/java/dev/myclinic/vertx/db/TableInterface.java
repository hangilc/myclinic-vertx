package dev.myclinic.vertx.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface TableInterface<DTO> extends Query.Projector<DTO>, SqlTranslator.TableInfo{

    String getTableName();
    void insert(Query query, DTO dto);
    void batchCopy(Query query, List<DTO> items);
    DTO getById(Query query, Object id);
    void update(Query query, DTO dto);
    void delete(Query query, Object id);
    DTO project(ResultSet rs, Query.ResultSetContext ctx) throws SQLException;
    DTO newInstance();

}

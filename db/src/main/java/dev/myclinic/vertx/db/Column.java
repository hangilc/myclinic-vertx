package dev.myclinic.vertx.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Column<DTO> {

    public interface StatementSetter<T> {
        void set(PreparedStatement stmt, int index, T source) throws SQLException;
    }

    public interface ResultSetGetter<T> {
        void getFromResultSet(ResultSet source, int index, T target) throws SQLException;
    }

    private final String dbColumnName;
    private final String dtoFieldName;
    private final boolean isPrimary;
    private final boolean isAutoIncrement;
    private final StatementSetter<DTO> getFromDTO;
    private final ResultSetGetter<DTO> putIntoDTO;
    private String placeHolder = "?";

    public Column(String dbColumnName, String dtoFieldName, boolean isPrimary, boolean isAutoIncrement,
                  StatementSetter<DTO> getFromDTO, ResultSetGetter<DTO> putIntoDTO) {
        this.dbColumnName = dbColumnName;
        this.dtoFieldName = dtoFieldName;
        this.isPrimary = isPrimary;
        this.isAutoIncrement = isAutoIncrement;
        this.getFromDTO = getFromDTO;
        this.putIntoDTO = putIntoDTO;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public String getDtoFieldName() {
        return dtoFieldName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public ResultSetGetter<DTO> putIntoDTO() {
        return putIntoDTO;
    }

    public StatementSetter<DTO> getFromDTO() {
        return getFromDTO;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    @Override
    public String toString() {
        return "Column{" +
                "dbColumnName='" + dbColumnName + '\'' +
                ", dtoFieldName='" + dtoFieldName + '\'' +
                ", isPrimary=" + isPrimary +
                ", isAutoIncrement=" + isAutoIncrement +
                ", placeHolder='" + placeHolder + '\'' +
                '}';
    }
}

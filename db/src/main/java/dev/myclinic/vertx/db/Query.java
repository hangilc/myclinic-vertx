package dev.myclinic.vertx.db;

import dev.myclinic.vertx.db.exception.IntegrityException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Query {

    private final Connection conn;
    private static boolean dumpSql = false;

    static {
        if (System.getProperty("dump-sql") != null) {
            dumpSql = true;
        }
    }

    public Query(Connection conn) {
        this.conn = conn;
    }

    public Connection getConnection() {
        return conn;
    }

    public interface SqlConsumer<T> {
        void accept(T t) throws SQLException;
    }

    public interface SqlBiConsumer<T, U> {
        void accept(T t, U u) throws SQLException;
    }

    public void exec(String sql, SqlConsumer<PreparedStatement> paramSetter) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            paramSetter.accept(stmt);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IntegrityException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(String sql, SqlConsumer<PreparedStatement> setter) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            setter.accept(stmt);
            int retVal = stmt.executeUpdate();
            stmt.close();
            return retVal;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(String sql, int autoGeneratedKeys, SqlConsumer<ResultSet> mapper,
                      SqlConsumer<PreparedStatement> setter) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql, autoGeneratedKeys);
            setter.accept(stmt);
            int retVal = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            mapper.accept(rs);
            stmt.close();
            return retVal;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void batchCopy(String sql, SqlBiConsumer<PreparedStatement, T> setter, List<T> items) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            int accum = 0;
            for (T item : items) {
                setter.accept(stmt, item);
                stmt.addBatch();
                accum += 1;
                int batchSize = 100;
                if (accum == batchSize) {
                    stmt.executeBatch();
                    accum = 0;
                }
            }
            if (accum > 0) {
                stmt.executeBatch();
            }
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void proc(String sql, Object... params) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index++, param);
            }
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IntegrityException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T get(String sql, Projector<T> projector,
                     Object... params) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index++, param);
            }
            ResultSet rs = stmt.executeQuery();
            T dto = null;
            if (rs.next()) {
                ResultSetContext ctx = new ResultSetContextImpl();
                dto = projector.project(rs, ctx);
            }
            stmt.close();
            return dto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(String sql, Object... params) {
        if (dumpSql) {
            System.out.println(sql);
        }
        return get(sql, (rs, ctx) -> rs.getInt(ctx.nextIndex()), params);
    }

    public interface ResultSetContext {
        int nextIndex();

        int probeIndex();
    }

    public interface Projector<T> {
        T project(ResultSet rs, ResultSetContext ctx) throws SQLException;
    }

    public static class NullableProjector<T> implements Projector<T> {

        private Projector<T> orig;
        private Function<T, Boolean> isNullTester;

        public NullableProjector(Projector<T> orig, Function<T, Boolean> isNullTester) {
            this.orig = orig;
            this.isNullTester = isNullTester;
        }

        @Override
        public T project(ResultSet rs, ResultSetContext ctx) throws SQLException {
            T t = orig.project(rs, ctx);
            return isNullTester.apply(t) ? null : t;
        }
    }

    private static class ResultSetContextImpl implements ResultSetContext {
        private int index = 1;

        @Override
        public int nextIndex() {
            return index++;
        }

        @Override
        public int probeIndex() {
            return index;
        }
    }

    public <T> List<T> query(String sql, Projector<T> projector, Object... params) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index++, param);
            }
            ResultSet rs = stmt.executeQuery();
            List<T> result = new ArrayList<>();
            while (rs.next()) {
                ResultSetContext ctx = new ResultSetContextImpl();
                T t = projector.project(rs, ctx);
                result.add(t);
            }
            stmt.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Pair<T, U> {
        public T first;
        public U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class Triple<P, Q, R> {
        public P first;
        public Q second;
        public R third;

        public Triple(P first, Q second, R third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    public <T, U> List<Pair<T, U>> query(String sql, Projector<T> proj1, Projector<U> proj2,
                                         Object... params) {
        if (dumpSql) {
            System.out.println(sql);
        }
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index++, param);
            }
            ResultSet rs = stmt.executeQuery();
            List<Pair<T, U>> result = new ArrayList<>();
            while (rs.next()) {
                ResultSetContext ctx = new ResultSetContextImpl();
                T t = proj1.project(rs, ctx);
                U u = proj2.project(rs, ctx);
                result.add(new Pair<>(t, u));
            }
            stmt.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

package dev.myclinic.vertx.master;

import dev.myclinic.vertx.master.csv.ShinryouMasterCSV;
import dev.myclinic.vertx.master.csv.ZipFileParser;
import dev.myclinic.vertx.master.db.DB;
import dev.myclinic.vertx.master.db.ShinryouMaster;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class ShinryouUpdater {

    private final Path zipFile;

    public ShinryouUpdater(Path zipFile) {
        this.zipFile = zipFile;
    }

    public void henkouKubun() throws SQLException, IOException, ClassNotFoundException {
        iter(csv -> {
            if (csv.kubun != 0) {
                System.out.printf("%d: %s\n", csv.kubun, csv.name);
            }
        });
    }

    public void dryRun() throws SQLException, ClassNotFoundException, IOException {
        Connection conn = DB.openConnection();
        iter(csv -> {
            try {
                ShinryouMaster cur = getCurrent(conn, csv.shinryoucode);
                if (csv.kubun == MasterConsts.HenkouKubunHaishi) {
                    if (cur != null) {
                        System.out.printf("[DELETE](%d) %s\n", csv.shinryoucode, csv.name);
                    }
                } else {
                    if (cur == null) {
                        System.out.printf("[NEW](%d) %s\n", csv.shinryoucode, csv.name);
                    } else {
                        if (!cur.equalsWithCSV(csv)) {
                            System.out.printf("[MODIFIED](%d) %s\n", csv.shinryoucode, csv.name);
                            printDifference(cur, csv);
                        }
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace(System.err);
                System.exit(1);
            }
        });
        conn.close();
    }

    public void update(LocalDate validFrom) throws Exception {
        LocalDate prev = validFrom.minus(1, ChronoUnit.DAYS);
        Connection conn = DB.openConnection();
        iter(csv -> {
            try {
                ShinryouMaster cur = getCurrent(conn, csv.shinryoucode);
                if (csv.kubun == MasterConsts.HenkouKubunHaishi) {
                    if (cur != null) {
                        if( cur.validFrom.equals(validFrom.toString()) ){
                            int n = deleteMaster(conn, csv.shinryoucode, validFrom);
                            if( n != 1 ){
                                throw new RuntimeException(String.format("Deleted columns is not one: %d %s\n",
                                        csv.shinryoucode, csv.name));
                            }
                            System.out.printf("[DELETED](%d) %s\n", csv.shinryoucode, csv.name);
                        } else {
                            if( validFrom.isAfter(LocalDate.parse(cur.validFrom)) ){
                                int n = updateValidUpto(conn, csv.shinryoucode,
                                        LocalDate.parse(cur.validFrom), prev);
                                if( n != 1 ){
                                    throw new RuntimeException(String.format("Deleted columns is not one: %d %s\n",
                                            csv.shinryoucode, csv.name));
                                }
                                System.out.printf("[UPDATED VALID-UPTO](%d) %s\n", csv.shinryoucode, csv.name);
                            } else {
                                throw new RuntimeException("Invalid validFrom value");
                            }
                        }
                    }
                } else {
                    if (cur == null) {
                        //System.out.printf("[NEW](%d) %s\n", csv.shinryoucode, csv.name);
                    } else {
                        if (!cur.equalsWithCSV(csv)) {
                            if( cur.validFrom.equals(validFrom.toString()) ){
                                int n = updateMaster(conn, csv, validFrom);
                                if( n != 1 ){
                                    throw new RuntimeException(String.format("Updated columns is not one: %d %s\n",
                                            csv.shinryoucode, csv.name));
                                }
                                System.out.printf("[UPDATED](%d) %s\n", csv.shinryoucode, csv.name);
                                System.out.printf("  old: %s\n", cur.toString());
                                System.out.printf("  new: %s\n", csv.toString());
                            } else {
                                if( validFrom.isAfter(LocalDate.parse(cur.validFrom)) ){
                                    invalidateAndEnter(conn, csv, validFrom);
                                    System.out.printf("[MODIFIED](%d) %s\n", csv.shinryoucode, csv.name);
                                    System.out.printf("  old: %s\n", cur.toString());
                                    System.out.printf("  new: %s\n", csv.toString());
                                }
                            }
                        }
                    }
                }
            } catch (Exception throwables) {
                throwables.printStackTrace(System.err);
                System.exit(1);
            }
        });
        conn.close();
    }

    private int countVisitShinryou(Connection conn, int shinryoucode, LocalDate validFrom)
        throws Exception {
        String sql = "select count(*) from visit_shinryou s, visit v where " +
                " s.shinryoucode = ? and s.visit_id = v.visit_id and date(v.v_datetime) >= ? ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, shinryoucode);
        stmt.setString(2, validFrom.toString());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int n = rs.getInt(1);
        rs.close();
        stmt.close();
        return n;
    }

    private int deleteMaster(Connection conn, int shinryoucode, LocalDate validFrom) throws Exception {
        int used = countVisitShinryou(conn, shinryoucode, validFrom);
        if( used > 0 ){
            throw new RuntimeException(String.format("Cannot delete used shinryou (%d)", shinryoucode));
        }
        String sql = "delete from shinryoukoui_master_arch where " +
                " shinryoucode = ? and valid_from = ? ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, shinryoucode);
        stmt.setString(2, validFrom.toString());
        int n = stmt.executeUpdate();
        stmt.close();
        return n;
    }

    private int updateMaster(Connection conn, ShinryouMasterCSV csv, LocalDate validFrom)
        throws Exception {
        String sql = "update shinryoukoui_master_arch set " +
                " name = ?, tensuu = ?, tensuu_shikibetsu = ?, shuukeisaki = ?," +
                " houkatsukensa = ?, oushinkubun = ?, kensagroup = ? " +
                " where shinryoucode = ? and valid_from = ? and " +
                " valid_upto = '0000-00-00' ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, csv.name);
        stmt.setString(2, csv.tensuu);
        stmt.setString(3, csv.tensuuShikibetsu);
        stmt.setString(4, csv.shuukeisaki);
        stmt.setString(5, csv.houkatsukensa);
        stmt.setString(6, csv.oushinKubun);
        stmt.setString(7, csv.kensaGroup);
        stmt.setInt(8, csv.shinryoucode);
        stmt.setString(9, validFrom.toString());
        int n = stmt.executeUpdate();
        stmt.close();
        return n;
    }

    private void enterMaster(Connection conn, ShinryouMasterCSV csv, LocalDate validFrom)
        throws Exception {
        String sql = "insert into shinryoukoui_master_arch set " +
                " name = ?, tensuu = ?, tensuu_shikibetsu = ?, shuukeisaki = ?," +
                " houkatsukensa = ?, oushinkubun = ?, kensagroup = ?, " +
                " shinryoucode = ?, valid_from = ?, " +
                " valid_upto = '0000-00-00' ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, csv.name);
        stmt.setString(2, csv.tensuu);
        stmt.setString(3, csv.tensuuShikibetsu);
        stmt.setString(4, csv.shuukeisaki);
        stmt.setString(5, csv.houkatsukensa);
        stmt.setString(6, csv.oushinKubun);
        stmt.setString(7, csv.kensaGroup);
        stmt.setInt(8, csv.shinryoucode);
        stmt.setString(9, validFrom.toString());
        stmt.executeUpdate();
        stmt.close();
    }

    private void invalidateAndEnter(Connection conn, ShinryouMasterCSV csv, LocalDate validFrom)
        throws Exception {
        LocalDate validUpto = validFrom.minus(1, ChronoUnit.DAYS);
        int n = updateValidUpto(conn, csv.shinryoucode, validFrom, validUpto);
        if( n != 1 ){
            throw new RuntimeException(String.format("Failed to set update : %d", csv.shinryoucode));
        }
        enterMaster(conn, csv, validFrom);
    }

    private int updateValidUpto(Connection conn, int shinryoucode, LocalDate validFrom, LocalDate validUpto)
        throws Exception {
        int used = countVisitShinryou(conn, shinryoucode, validFrom);
        if( used > 0 ){
            throw new RuntimeException(String.format("Cannot invalidate used shinryou (%d)", shinryoucode));
        }
        String sql = "update shinryoukoui_master_arch set valid_upto = ? where shinryoucode = ? and " +
                " valid_upto = '0000-00-00' ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, validUpto.toString());
        stmt.setInt(2, shinryoucode);
        int n = stmt.executeUpdate();
        stmt.close();
        return n;
    }

    private void printDifference(ShinryouMaster master, ShinryouMasterCSV csv) {
        if (!master.name.equals(csv.name)) {
            System.out.printf("  name: %s -> %s\n", master.name, csv.name);
        }
        if (!master.tensuu.equals(csv.tensuu)) {
            System.out.printf("  tensuu: %s -> %s\n", master.tensuu, csv.tensuu);
        }
    }

    void iter(Consumer<ShinryouMasterCSV> consumer)
            throws SQLException, ClassNotFoundException, IOException {
        ZipFileParser.iterShinryouZipFile(zipFile.toFile(), consumer);
    }

    public ShinryouMaster getCurrent(Connection conn, int shinryoucode) throws SQLException {
        String sql = "select * from shinryoukoui_master_arch where shinryoucode = ? " +
                " and valid_upto = '0000-00-00' ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, shinryoucode);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            rs.close();
            stmt.close();
            return null;
        }
        ShinryouMaster m = ShinryouMaster.fromResultSet(rs);
        if (rs.next()) {
            throw new RuntimeException(String.format("Multiple masters for shinryoucode %d", shinryoucode));
        }
        rs.close();
        stmt.close();
        return m;
    }

}

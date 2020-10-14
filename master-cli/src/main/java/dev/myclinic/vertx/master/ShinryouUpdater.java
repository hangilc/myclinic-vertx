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
import java.util.function.Consumer;

public class ShinryouUpdater {

    private final Path zipFile;

    public ShinryouUpdater(Path zipFile) {
        this.zipFile = zipFile;
    }

    public void henkouKubun() throws SQLException, IOException, ClassNotFoundException {
        iter(csv -> {
            if( csv.kubun != 0 ){
                System.out.printf("%d: %s\n", csv.kubun, csv.name);
            }
        });
    }

    public void dryRun() throws SQLException, ClassNotFoundException, IOException {
        Connection conn = DB.openConnection();
        iter(csv -> {
            try {
                ShinryouMaster cur = getCurrent(conn, csv.shinryoucode);
                if( cur == null ){
                    System.out.printf("[NEW] %s\n", csv.name);
                } else {
                    if( !cur.equalsWithCSV(csv) ){
                        System.out.printf("[MODIFIED] %s\n", cur.name);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace(System.err);
                System.exit(1);
            }
        });
        conn.close();
    }

    public void iter(Consumer<ShinryouMasterCSV> consumer)
            throws SQLException, ClassNotFoundException, IOException {
        ZipFileParser.iterShinryouZipFile(zipFile.toFile(), consumer);
    }

    public ShinryouMaster getCurrent(Connection conn, int shinryoucode) throws SQLException {
        String sql = "select * from shinryoukoui_master_arch where shinryoucode = ? " +
                " and valid_upto = '0000-00-00' ";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, shinryoucode);
        ResultSet rs = stmt.executeQuery();
        if( !rs.next() ){
            rs.close();
            stmt.close();
            return null;
        }
        ShinryouMaster m = ShinryouMaster.fromResultSet(rs);
        if( rs.next() ){
            throw new RuntimeException(String.format("Multiple masters for shinryoucode %d", shinryoucode));
        }
        rs.close();
        stmt.close();
        return m;
    }

}

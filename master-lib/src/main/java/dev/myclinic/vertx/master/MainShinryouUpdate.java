package dev.myclinic.vertx.master;

import dev.myclinic.vertx.master.csv.ZipFileParser;
import dev.myclinic.vertx.master.db.DB;
import dev.myclinic.vertx.master.db.ShinryouMaster;

import java.sql.*;

public class MainShinryouUpdate {

    public static void usage() {
        System.err.println("MainShinryouCSV shinryou-zip-file [exec]");
    }

    public static void main(String[] args) throws Exception {
        String zipFile = null;
        boolean exec = false;
        if( args.length >= 1 ){
            zipFile = args[0];
        }
        if( args.length >= 2 ){
            if( args[1].equals("exec") ){
                exec = true;
            } else {
                System.err.println("'exec' expected as second arg");
                usage();
                System.exit(1);
            }
        }
        if (zipFile == null) {
            usage();
            System.exit(1);
        }
        Connection conn = DB.openConnection();
        ZipFileParser.iterShinryouZipFile(zipFile, csv -> {
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

    public static ShinryouMaster getCurrent(Connection conn, int shinryoucode) throws SQLException {
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

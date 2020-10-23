package dev.myclinic.vertx.adhoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Koukikourei8Digits {

    private static class Data {
        int koukikoureiId;
        int patientId;
        String hihokensha;

        public Data(int koukikoureiId, int patientId, String hihokensha) {
            this.koukikoureiId = koukikoureiId;
            this.patientId = patientId;
            this.hihokensha = hihokensha;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "koukikoureiId=" + koukikoureiId +
                    ", patientId=" + patientId +
                    ", hihokensha='" + hihokensha + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {
        List<Data> dataList = new ArrayList<>();
        Connection conn = Misc.openConnection();
        {
            String sql = "select koukikourei_id, patient_id, hihokensha_bangou " +
                    " from hoken_koukikourei where length(hihokensha_bangou) < 8";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int koukikoureiId = rs.getInt("koukikourei_id");
                int patientId = rs.getInt("patient_id");
                String hihokensha = rs.getString("hihokensha_bangou");
                int inum = Integer.parseInt(hihokensha);
                String fixed = String.format("%08d", inum);
                Data data = new Data(koukikoureiId, patientId, fixed);
                dataList.add(data);
            }
            rs.close();
            stmt.close();
        }
        for(Data data: dataList){
            System.out.printf("%d(%d): %s\n", data.koukikoureiId, data.patientId, data.hihokensha);
            String sql = "update hoken_koukikourei set hihokensha_bangou = ? where koukikourei_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, data.hihokensha);
            stmt.setInt(2, data.koukikoureiId);
            int n = stmt.executeUpdate();
            if( n != 1 ){
                throw new RuntimeException(String.format("Failed to update %d", data.koukikoureiId));
            }
        }
        conn.close();
    }
}

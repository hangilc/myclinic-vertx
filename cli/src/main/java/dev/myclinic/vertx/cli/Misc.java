package dev.myclinic.vertx.cli;

import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.util.DateTimeUtil;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class Misc {

    public static DataSource getDataSource(){
        return MysqlDataSourceFactory.create();
    }

    public static int ageAt(LocalDate birthday, LocalDate at){
        return DateTimeUtil.calcAge(birthday, at);
    }

    public static String makeTimestamp(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
        return now.format(formatter);
    }

    public static List<String> readLines(String file){
        try(InputStream ins = new FileInputStream(file) ){
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            List<String> lines = new ArrayList<>();
            String line;
            while( (line = reader.readLine()) != null ){
                line = line.trim();
                lines.add(line);
            }
            return lines;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}

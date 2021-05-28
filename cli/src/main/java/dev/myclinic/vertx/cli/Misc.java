package dev.myclinic.vertx.cli;

import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.util.DateTimeUtil;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class Misc {

    public static DataSource getDataSource() {
        return MysqlDataSourceFactory.create();
    }

    public static int ageAt(LocalDate birthday, LocalDate at) {
        return DateTimeUtil.calcAge(birthday, at);
    }

    public static String makeTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
        return now.format(formatter);
    }

    public static List<String> readLines(String file) {
        return readLines(file, () -> {
            throw new RuntimeException("File does not exist: " + file);
        });
    }

    public static List<String> readLines(String file, Supplier<List<String>> ifFileMissing){
        if( Files.exists(Path.of(file)) ) {
            try (InputStream ins = new FileInputStream(file)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(ins, StandardCharsets.UTF_8));
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    lines.add(line);
                }
                return lines;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return ifFileMissing.get();
        }
    }

    public static void saveLines(String file, List<String> lines) {
        try (OutputStream os = new FileOutputStream(file);
             PrintWriter writer = new PrintWriter(os)) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveString(String file, String content) {
        try {
            Files.writeString(Path.of(file), content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

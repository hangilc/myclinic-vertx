package dev.myclinic.vertx.cli;

import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.util.DateTimeUtil;
import dev.myclinic.vertx.util.kanjidate.GengouNenPair;
import dev.myclinic.vertx.util.kanjidate.KanjiDate;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

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

    public static Path backupFile(Path src, Path archDir, Function<String, String> filePartRewriter)
            throws IOException {
        File srcFile = src.toFile();
        String parent = srcFile.getParent();
        String file = srcFile.getName();
        Pattern pat = Pattern.compile("(.+?)(\\.[^.]*)?$");
        Matcher m = pat.matcher(file);
        String backupFile;
        String ts = makeTimestamp();
        if( m.matches() ){
            backupFile = String.format("%s-%s%s", filePartRewriter.apply(m.group(1)), ts, m.group(2));
        } else {
            backupFile = file + "-" + ts;
        }
        Path dst = archDir.resolve(backupFile);
        Files.move(src, dst);
        return dst;
    }

    public static Path backupFile(Path src, Path archDir) throws IOException {
        return backupFile(src, archDir, s -> s);
    }

    public static List<String> readLines(String file) {
        return readLines(file, () -> {
            throw new RuntimeException("File does not exist: " + file);
        });
    }

    public static List<String> readLines(Path path) {
        return readLines(path.toString());
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

    public static void saveLines(String file, List<String> lines, Charset charset) {
        try (OutputStream os = new FileOutputStream(file);
             PrintWriter writer = new PrintWriter(os, false, charset)) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveLines(String file, List<String> lines) {
        saveLines(file, lines, StandardCharsets.UTF_8);
    }

    public static void saveString(String file, String content) {
        try {
            Files.writeString(Path.of(file), content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void appendLines(String file, List<String> lines) throws Exception {
        try(FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw)){
            lines.forEach(pw::println);
        }
    }

    public static void ensureDirectory(String dirPath) throws Exception {
        ensureDirectory(Path.of(dirPath));
    }

    public static void ensureDirectory(Path path) throws Exception {
        if( !Files.exists(Path.of(path.toString())) ){
            Files.createDirectories(path);
        }
    }

    public static String youbiIndexToKanji(int index){
        return DateTimeUtil.youbiIndexToKanji(index);
    }

    public static String youbiAsKanji(DayOfWeek dow){
        return DateTimeUtil.youbiAsKanji(dow);
    }

    public static String localDateToKanji(LocalDate date, boolean twoDigits, boolean youbi){
        GengouNenPair geng = KanjiDate.yearToGengou(date);
        return String.format("%s%s年%s月%s日%s",
                geng.gengou.getKanjiRep(),
                twoDigits ? String.format("%02d", geng.nen) : String.format("%d", geng.nen),
                twoDigits ? String.format("%02d", date.getMonthValue()) : String.format("%d", date.getMonthValue()),
                twoDigits ? String.format("%02d", date.getDayOfMonth()) : String.format("%d", date.getDayOfMonth()),
                youbi ? String.format("（%s）", youbiAsKanji(date.getDayOfWeek())) : ""
        );
    }

    private final static Random random = new Random();

    public static <T> T chooseRandom(List<T> candidates){
        if( candidates.size() == 0 ){
            return null;
        } else {
            int i = random.nextInt(candidates.size());
            return candidates.get(i);
        }
    }
}

package dev.myclinic.mastermap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MasterChronoMap {

    private static class Entry {
        int fromCode;
        LocalDate changeDate;
        int toCode;

        Entry(int fromCode, LocalDate changeDate, int toCode){
            this.fromCode = fromCode;
            this.changeDate = changeDate;
            this.toCode = toCode;
        }
    }

    private Map<MasterKind, List<Entry>> map = new HashMap<>();
    {
        map.put(MasterKind.Shinryou, new ArrayList<>());
        map.put(MasterKind.Yakuzai, new ArrayList<>());
        map.put(MasterKind.Kizai, new ArrayList<>());
        map.put(MasterKind.Byoumei, new ArrayList<>());
        map.put(MasterKind.Shuushokugo, new ArrayList<>());
    }

    public void put(MasterKind kind, int fromCode, LocalDate changeDate, int toCode){
        map.get(kind).add(new Entry(fromCode, changeDate, toCode));
    }

    public int resolve(MasterKind kind, int fromCode, LocalDate at){
        int code = fromCode;
        for(Entry entry: map.get(kind)){
            if( entry.fromCode == code && isApplicable(entry.changeDate, at) ){
                code = entry.toCode;
            }
        }
        return code;
    }

    private boolean isApplicable(LocalDate changeDate, LocalDate at){
        return at.equals(changeDate) || at.isAfter(changeDate);
    }

    private static final Pattern pattern =
            Pattern.compile("^(Y|S|K|D|A),(\\d+),(\\d{4}-\\d{2}-\\d{2}),(\\d+)");

    public void processLine(String line){
        Matcher matcher = pattern.matcher(line);
        if( matcher.find() ){
            String kindStr = matcher.group(1);
            String fromCodeStr = matcher.group(2);
            String changeDateStr = matcher.group(3);
            String toCodeStr = matcher.group(4);
            MasterKind kind = charToKind(kindStr)
                    .orElseThrow(() -> new RuntimeException("Invalid kind: " + line));
            int fromCode = strToInt(fromCodeStr)
                    .orElseThrow(() -> new RuntimeException("Invalid from-code: " + line));
            LocalDate changeDate = strToDate(changeDateStr)
                    .orElseThrow(() -> new RuntimeException("Invalid change-date: " + line));
            int toCode = strToInt(toCodeStr)
                    .orElseThrow(() -> new RuntimeException("Invalid to-code: " + line));
            put(kind, fromCode, changeDate, toCode);
        }
    }

    public static MasterChronoMap fromFile(File file) throws IOException {
        MasterChronoMap map = new MasterChronoMap();
        Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(map::processLine);
        return map;
    }

    private Optional<MasterKind> charToKind(String ch){
        switch(ch){
            case "S": return Optional.of(MasterKind.Shinryou);
            case "K": return Optional.of(MasterKind.Kizai);
            case "D": return Optional.of(MasterKind.Byoumei);
            case "A": return Optional.of(MasterKind.Shuushokugo);
            case "Y": return Optional.of(MasterKind.Yakuzai);
            default: return Optional.empty();
        }
    }

    private Optional<Integer> strToInt(String str){
        try {
            return Optional.of(Integer.parseInt(str));
        } catch(Exception e){
            return Optional.empty();
        }
    }

    private Optional<LocalDate> strToDate(String str){
        try {
            return Optional.of(LocalDate.parse(str));
        } catch(Exception e){
            return Optional.empty();
        }
    }

    private boolean isBlankLine(String line){
        return line.isBlank();
    }

    private boolean isCommentLine(String line){
        return line.startsWith(";");
    }

}

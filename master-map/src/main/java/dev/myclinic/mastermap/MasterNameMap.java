package dev.myclinic.mastermap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MasterNameMap {

    private final Map<MasterKind, Map<String, Integer>> map = new HashMap<>();
    {
        map.put(MasterKind.Shinryou, new HashMap<>());
        map.put(MasterKind.Yakuzai, new HashMap<>());
        map.put(MasterKind.Kizai, new HashMap<>());
        map.put(MasterKind.Byoumei, new HashMap<>());
        map.put(MasterKind.Shuushokugo, new HashMap<>());
    }

    public void put(MasterKind kind, String name, int code){
        map.get(kind).put(name, code);
    }

    public Optional<Integer> resolve(MasterKind kind, String name){
        Map<String, Integer> m = map.get(kind);
        if( m.containsKey(name) ){
            return Optional.of(m.get(name));
        } else {
            return Optional.empty();
        }
    }

    private static final Pattern pattern = Pattern.compile("^(s|k|d|a|y),([^,]+),(\\d+)");

    public void processLine(String line){
        Matcher matcher = pattern.matcher(line);
        if( matcher.find() ){
            String kindStr = matcher.group(1);
            String nameStr = matcher.group(2);
            String codeStr = matcher.group(3);
            Optional<MasterKind> optKind = charToKind(kindStr);
            MasterKind kind = optKind.orElseThrow(() -> new RuntimeException("Unknown kind: " + line));
            try {
                int code = Integer.parseInt(codeStr);
                put(kind, nameStr, code);
            } catch(NumberFormatException e){
                throw new RuntimeException("Invalid code: " + line);
            }
        } else {
            if( !(isBlankLine(line) || isCommentLine(line)) ){
                throw new RuntimeException("Invalid line: " + line);
            }
        }
    }

    public static MasterNameMap fromFile(File file) throws IOException {
        MasterNameMap map = new MasterNameMap();
        Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(map::processLine);
        return map;
    }

    private boolean isBlankLine(String line){
        return line.isBlank();
    }

    private boolean isCommentLine(String line){
        return line.startsWith(";");
    }

    private Optional<MasterKind> charToKind(String ch){
        switch(ch){
            case "s": return Optional.of(MasterKind.Shinryou);
            case "k": return Optional.of(MasterKind.Kizai);
            case "d": return Optional.of(MasterKind.Byoumei);
            case "a": return Optional.of(MasterKind.Shuushokugo);
            case "y": return Optional.of(MasterKind.Yakuzai);
            default: return Optional.empty();
        }
    }

}

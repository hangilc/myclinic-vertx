package dev.myclinic.vertx.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public class SqlTranslator {

    public interface TableInfo {
        String getDtoName();
        String getDbTableName();
        Map<String, String> getDtoFieldToDbColumnMap();
        List<String> getColumnNames();
    }

    public static class AliasedTable {
        public TableInfo table;
        public String alias;

        public AliasedTable(TableInfo table, String alias) {
            this.table = table;
            this.alias = alias;
        }
    }

    public String translate(String src, TableInfo table){
        return translate(src, List.of(new AliasedTable(table, "")));
    }

    public String translate(String src, TableInfo table, String alias){
        return translate(src, List.of(new AliasedTable(table, alias)));
    }

    public String translate(String src, TableInfo table1, String alias1, TableInfo table2, String alias2){
        return translate(src, List.of(new AliasedTable(table1, alias1), new AliasedTable(table2, alias2)));
    }

    public String translate(String src, TableInfo table1, String alias1, TableInfo table2, String alias2,
                            TableInfo table3, String alias3){
        return translate(src, List.of(
                new AliasedTable(table1, alias1),
                new AliasedTable(table2, alias2),
                new AliasedTable(table3, alias3)
        ));
    }

    public String translate(String src, TableInfo table1, String alias1, TableInfo table2, String alias2,
                            TableInfo table3, String alias3, TableInfo table4, String alias4){
        return translate(src, List.of(
                new AliasedTable(table1, alias1),
                new AliasedTable(table2, alias2),
                new AliasedTable(table3, alias3),
                new AliasedTable(table4, alias4)
        ));
    }

    private static final Pattern selectFromPattern = Pattern.compile("^\\s*select\\s+\\*\\s+from\\b",
            Pattern.CASE_INSENSITIVE);

    public String translate(String src, List<AliasedTable> tables){
        Map<String, String> rewriteMap = new HashMap<>();
        List<String> regexToks = new ArrayList<>();
        if( tables.size() == 1 ){
            AliasedTable table = tables.get(0);
            if( table.alias == null || table.alias.isEmpty() ) {
                Matcher matcher = selectFromPattern.matcher(src);
                src = matcher.replaceFirst("select * from");
                regexToks.add("^select \\* from\\b");
                rewriteMap.put("select * from",  "select " + cols(table.table.getColumnNames()) + " from");
            }
        }
        for(AliasedTable at: tables){
            TableInfo table = at.table;
            String alias = at.alias;
            regexToks.add("\\b" + table.getDtoName() + "\\b");
            rewriteMap.put(table.getDtoName(), table.getDbTableName());
            if( alias != null && !alias.isEmpty() ) {
                regexToks.add("\\b" + alias + "\\.\\*");
                rewriteMap.put(alias + ".*", cols(table.getColumnNames(), alias));
            }
            for(Map.Entry<String, String> entry: table.getDtoFieldToDbColumnMap().entrySet()){
                String dtoField = entry.getKey();
                String colName = entry.getValue();
                if( alias == null || alias.isEmpty() ){
                    regexToks.add("\\b" + dtoField + "\\b");
                    rewriteMap.put(dtoField, colName);
                } else {
                    regexToks.add("\\b" + alias + "\\." + dtoField + "\\b");
                    rewriteMap.put(alias + "." + dtoField, alias + "." + colName);
                }
            }
        }
        String regex = "(" + String.join("|", regexToks) + ")";
        Pattern pat = Pattern.compile(regex);
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pat.matcher(src);
        while( matcher.find() ){
            String g = matcher.group(1);
            matcher.appendReplacement(sb, rewriteMap.get(g));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String cols(List<String> columns){
        return columns.stream().collect(joining(","));
    }

    private String cols(List<String> columns, String alias){
        if( alias == null || alias.isEmpty() ){
            return cols(columns);
        }
        return columns.stream().map(c -> alias + "." + c).collect(joining(","));
    }

}

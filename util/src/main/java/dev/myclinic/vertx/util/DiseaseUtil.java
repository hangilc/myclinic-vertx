package dev.myclinic.vertx.util;

import dev.myclinic.vertx.consts.Shuushokugo;
import dev.myclinic.vertx.dto.ByoumeiMasterDTO;
import dev.myclinic.vertx.dto.DiseaseFullDTO;
import dev.myclinic.vertx.dto.ShuushokugoMasterDTO;
import dev.myclinic.vertx.util.kanjidate.KanjiDateRepBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiseaseUtil {

    public static boolean isPrefix(int shuushokugocode){
        return shuushokugocode < Shuushokugo.PostFixStart;
    }

    public static String getFullName(ByoumeiMasterDTO byoumeiMaster, List<ShuushokugoMasterDTO> adjList){
        List<String> pre = new ArrayList<>();
        List<String> post = new ArrayList<>();
        adjList.forEach(adjMaster -> {
            String name = adjMaster.name;
            if( isPrefix(adjMaster.shuushokugocode) ){
                pre.add(name);
            } else {
                post.add(name);
            }
        });
        String byoumeiName = byoumeiMaster == null ? "" : byoumeiMaster.name;
        return String.join("", pre) + byoumeiName + String.join("", post);

    }

    public static String getFullName(DiseaseFullDTO diseaseFull){
        return getFullName(diseaseFull.master,
                diseaseFull.adjList.stream().map(adj -> adj.master).collect(Collectors.toList()));
    }

    private static String formatDate(String sqldate, String unspecified, Function<LocalDate, String> formatter){
        if( sqldate == null || "0000-00-00".equals(sqldate) ){
            return unspecified;
        } else {
            LocalDate date = LocalDate.parse(sqldate);
            return formatter.apply(date);
        }
    }

    public static String formatDateAsKanji(String sqldate){
        return formatDate(sqldate, "未定",
                date -> new KanjiDateRepBuilder(date).format1().build());
    }

    public static String formatDate(String sqldate){
        return formatDate(sqldate, "未定");
    }

    public static String formatDate(String sqldate, String unspecified){
        return formatDate(sqldate, unspecified,
                date -> new KanjiDateRepBuilder(date).format5().build());
    }
}

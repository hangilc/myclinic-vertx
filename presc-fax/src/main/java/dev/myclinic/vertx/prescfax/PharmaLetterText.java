package dev.myclinic.vertx.prescfax;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PharmaLetterText {

    public static String createFromData(Data data) {
        LocalDate fromDate = LocalDate.parse(data.dateFrom);
        LocalDate uptoDate = LocalDate.parse(data.dateUpto);
        String dateRange = String.format(
                "%d年%d月%d日から%d年%d月%d日までに当院からファックスした処方箋の原本です。",
                fromDate.getYear(), fromDate.getMonthValue(), fromDate.getDayOfMonth(),
                uptoDate.getYear(), uptoDate.getMonthValue(), uptoDate.getDayOfMonth());
        List<String> pages = new ArrayList<>();
        for (ShohousenGroup group : data.groups) {
            StringBuilder sb = new StringBuilder();
            line(sb, group.pharmacy.name);
            line(sb, "担当者様");
            line(sb);
            line(sb, dateRange);
            line(sb);
            for (Presc presc : group.items) {
                LocalDate issue = LocalDate.parse(presc.visit.visitedAt.substring(0, 10));
                String s = String.format("%s%s %d年%d月%d日",
                        presc.patient.lastName,
                        presc.patient.firstName,
                        issue.getYear(),
                        issue.getMonthValue(),
                        issue.getDayOfMonth());
                line(sb, s);
            }
            line(sb);
            line(sb, data.clinicInfo.address);
            line(sb, data.clinicInfo.phone);
            line(sb, data.clinicInfo.name);
            line(sb, data.clinicInfo.doctorName, false);
            pages.add(sb.toString());
        }
        return String.join("\n{{ new-page }}\n", pages);
    }

    private static void line(StringBuilder sb, String s, boolean addNewline) {
        sb.append(s);
        if (addNewline) {
            sb.append("\n");
        }
    }

    private static void line(StringBuilder sb, String s) {
        line(sb, s, true);
    }

    private static void line(StringBuilder sb) {
        line(sb, "", true);
    }

}

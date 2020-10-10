package dev.myclinic.vertx.drawer;

import java.util.ArrayList;
import java.util.List;

public class LineBreaker2 {

    public static class Slice {
        public int start;
        public int end;

        public Slice(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public static List<Slice> breakToLines(List<Double> cws, double lineWidth) {
        if( cws.size() == 0 ){
            return List.of(new Slice(0, 0));
        }
        List<Slice> result = new ArrayList<>();
        int curStart = 0;
        int curEnd = 0;
        double curWidth = 0;
        int i = 0;
        for (double cw : cws) {
            if (curStart != curEnd && curWidth + cw > lineWidth) {
                result.add(new Slice(curStart, curEnd));
                curStart = curEnd;
                curWidth = 0;
            }
            curWidth += cw;
            curEnd = i + 1;
            i += 1;
        }
        if (curStart < curEnd) {
            result.add(new Slice(curStart, curEnd));
        }
        return result;
    }

}

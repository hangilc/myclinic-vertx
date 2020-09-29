package dev.myclinic.vertx.rcpt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class CFUtil {

    private CFUtil() {
    }

    public static <D> CompletableFuture<Void> forEach(List<D> dataList, Function<D, CompletableFuture<Void>> cvt) {
        CompletableFuture<Void> cfMain = new CompletableFuture<>();
        forEachIter(cfMain, dataList, cvt);
        return cfMain;
    }

    private static <D> void forEachIter(CompletableFuture<Void> cfMain, List<D> dataList,
                                        Function<D, CompletableFuture<Void>> cvt) {
        if (dataList.size() == 0) {
            cfMain.complete(null);
        } else {
            D data = dataList.remove(0);
            CompletableFuture<Void> cf = cvt.apply(data);
            cf.whenComplete((result, ex) -> {
                if (ex == null) {
                    forEachIter(cfMain, dataList, cvt);
                } else {
                    cfMain.completeExceptionally(ex);
                }
            });
        }
    }

    public static <D, T> CompletableFuture<List<T>> mapSer(List<D> dataList, Function<D, CompletableFuture<T>> cvt) {
        CompletableFuture<List<T>> cfMain = new CompletableFuture<>();
        List<T> accum = new ArrayList<>();
        mapIterSer(cfMain, accum, dataList, cvt);
        return cfMain;
    }

    private static <D, T> void mapIterSer(CompletableFuture<List<T>> cfMain, List<T> accum,
                                          List<D> dataList, Function<D, CompletableFuture<T>> cvt) {
        if (dataList.size() == 0) {
            cfMain.complete(accum);
        } else {
            D data = dataList.remove(0);
            CompletableFuture<T> cf = cvt.apply(data);
            cf.whenComplete((result, ex) -> {
                if (ex == null) {
                    accum.add(result);
                    mapIterSer(cfMain, accum, dataList, cvt);
                } else {
                    cfMain.completeExceptionally(ex);
                }
            });
        }
    }

    public static <D, T> CompletableFuture<List<T>> map(List<D> dataList, Function<D, CompletableFuture<T>> cvt) {
        CompletableFuture<List<T>> cfMain = new CompletableFuture<>();
        List<T> accum = new ArrayList<>(dataList.size());
        if (dataList.size() == 0) {
            cfMain.complete(accum);
        } else {
            mapIter(cfMain, accum, dataList, cvt);
        }
        return cfMain;
    }

    private static <D, T> void mapIter(CompletableFuture<List<T>> cfMain, List<T> accum,
                                       List<D> dataList, Function<D, CompletableFuture<T>> cvt) {
        int n = dataList.size();
        for(int i=0;i<n;i++){
            accum.add(null);
        }
        AtomicInteger done = new AtomicInteger(0);
        for(int i=0;i<n;i++){
            final int index = i;
            cvt.apply(dataList.get(i))
                    .thenAccept(result -> {
                        accum.set(index, result);
                        int doneValue = done.addAndGet(1);
                        if( doneValue == n ){
                            cfMain.complete(accum);
                        }
                    })
                    .exceptionally(ex -> {
                        cfMain.completeExceptionally(ex);
                        return null;
                    });
        }
    }

}

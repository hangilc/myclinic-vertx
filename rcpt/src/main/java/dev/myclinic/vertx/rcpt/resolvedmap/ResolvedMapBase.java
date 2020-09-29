package dev.myclinic.vertx.rcpt.resolvedmap;

import dev.myclinic.vertx.mastermap.MasterNameMap;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResolvedMapBase {

    //private static Logger logger = LoggerFactory.getLogger(ResolvedMapBase.class);

    public interface Resolver {
        CompletableFuture<Map<String, Integer>> resolve(LocalDate at, List<List<String>> args);
    }

    private Map<String, Integer> nameMap;

    public Map<String, Integer> getNameMap(){
        return nameMap;
    }

    public CompletableFuture<Void> resolveAt(LocalDate at, Resolver resolver) {
        List<List<String>> args = makeResolveArgs();
        return resolver.resolve(at, args)
                .thenAccept(map -> {
                    this.nameMap = map;
                    try {
                        for (Field fld : this.getClass().getFields()) {
                            if (fld.getType() == Integer.TYPE) {
                                String name = fld.getName();
                                if (map.containsKey(name)) {
                                    fld.setInt(this, map.get(name));
                                }
                            }
                        }
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    private List<List<String>> makeResolveArgs(){
        List<List<String>> args = new ArrayList<>();
        for (Field fld : this.getClass().getFields()) {
            if (fld.getType() == Integer.TYPE) {
                List<String> arg = new ArrayList<>();
                arg.add(fld.getName());
                if (fld.isAnnotationPresent(MasterNameMapAnnot.class)) {
                    MasterNameMapAnnot annot = fld.getAnnotation(MasterNameMapAnnot.class);
                    arg.addAll(Arrays.asList(annot.candidates()));
                }
                args.add(arg);
            }
        }
        return args;
    }

    public List<String> getUnresolved(){
        List<String> unresolved = new ArrayList<>();
        try {
            for (Field fld : this.getClass().getFields()) {
                if (fld.getType() == Integer.TYPE) {
                    int value = fld.getInt(this);
                    if( value == 0 ){
                        unresolved.add(fld.getName());
                    }
                }
            }
        } catch(IllegalAccessException ex){
            throw new RuntimeException(ex);
        }
        return unresolved;
    }

}

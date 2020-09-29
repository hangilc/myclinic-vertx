package dev.myclinic.vertx.rcpt.create;

import dev.myclinic.vertx.client.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Gendogaku {

    private int patientId;
    private String name;
    private String kubun;

    private static Map<Integer, Gendogaku> registry = new HashMap<>();

    public static void readFromFile(String fileName) {
        try {
            for(String line: Files.readAllLines(Paths.get(fileName))){
                line = line.trim();
                if( line.isEmpty() ){
                    continue;
                }
                String[] parts = line.split("\\s+");
                if( parts.length != 3 ){
                    throw new RuntimeException("Invalid gendogaku: " + line);
                }
                Gendogaku g = new Gendogaku(Integer.parseInt(parts[0]), parts[1], parts[2]);
                registry.put(g.patientId, g);
                Service.api.getPatient(g.patientId)
                        .thenAccept(p -> {
                            if( !g.name.equals(p.lastName + p.firstName) ){
                                throw new RuntimeException("Invalid patient name: " + g.name);
                            }
                        }).join();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String getTokijikou(int patientId){
        Gendogaku g = registry.get(patientId);
        if( g == null ){
            return null;
        } else {
            switch(g.kubun){
                case "現役3": case "現役並3": return "26区ア";
                case "現役2": case "現役並2": return "27区イ";
                case "現役1": case "現役並1": return "28区ウ";
                case "2": case "1": return "30区オ";
                default: throw new RuntimeException("Unknown gendogaku kubun: " + g.kubun);
            }
        }
    }

    private Gendogaku(int patientId, String name, String kubun) {
        this.patientId = patientId;
        this.name = name;
        this.kubun = kubun;
    }

    @Override
    public String toString() {
        return "Gendogaku{" +
                "patientId=" + patientId +
                ", kubun='" + kubun + '\'' +
                '}';
    }
}

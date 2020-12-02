package dev.myclinic.vertx.drawersite;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class UploadJobHandlers {

    private static final Random random = new Random();

    private UploadJobHandlers(){

    }

    static void createJob(Handler handler) throws Exception {
        switch(handler.getMethod()){
            case "OPTIONS": {
                handler.respondToOptions(List.of("POST", "OPTIONS"));
                break;
            }
            case "POST": {
                handler.allowCORS();
                UploadJob job = Main.mapper.readValue(handler.getBody(), UploadJob.class);
                String jobName = createJobName(job.patientId);
                Path jobFile = getJobDir().resolve(jobName + ".json");
                Main.mapper.writeValue(jobFile.toFile(), job);
                handler.sendJson(jobName);
                break;
            }
            default: {
                handler.sendError("Invalid upload-job access.");
                break;
            }
        }
    }

    static void listJobs(Handler handler) throws Exception {
        switch(handler.getMethod()){
            case "OPTIONS": {
                handler.respondToOptions(List.of("GET", "OPTIONS"));
                break;
            }
            case "GET": {
                handler.allowCORS();
                Path dir = getJobDir();
                List<String> result = new ArrayList<>();
                for(Path p: Files.newDirectoryStream(dir)){
                    result.add(p.getFileName().toString());
                }
                handler.sendJson(result);
                break;
            }
            default: {
                handler.sendError("Invalid upload-job access.");
                break;
            }
        }
    }

    static void handleJob(Handler handler, String jobName) throws Exception {
        switch(handler.getMethod()){
            case "OPTIONS": {
                handler.respondToOptions(List.of("DELETE", "OPTIONS"));
                break;
            }
            case "DELETE": {
                handler.allowCORS();
                Path dir = getJobDir();
                Path file = dir.resolve(jobName + ".json");
                if( Files.exists(file) ){
                    Files.delete(file);
                    handler.sendJson(true);
                } else {
                    handler.sendError("そのような upload job は見つかりませんでした。");
                }
                break;
            }
            default: {
                handler.sendError("Invalid upload-job access.");
                break;
            }
        }
    }

    private static char getRandomChar(){
        int i = random.nextInt(52);
        if( i < 26 ){
            return (char)((int)'A' + i);
        } else {
            return (char)((int)'a' + i - 26);
        }
    }

    private static String getRandomString(int len){
        char[] chars = new char[len];
        for(int i=0;i<len;i++){
            chars[i] = getRandomChar();
        }
        return new String(chars);
    }

    private static String createJobName(int patientId){
        LocalDateTime dt = LocalDateTime.now();
        return String.format("%d-%04d%02d%02d-%02d%02d%02d-%s",
                patientId,
                dt.getYear(),
                dt.getMonthValue(),
                dt.getDayOfMonth(),
                dt.getHour(),
                dt.getMinute(),
                dt.getSecond(),
                getRandomString(6));
    }

    private static Path getJobDir() throws Exception {
        Path root = Main.getDataDir();
        Path dir = root.resolve("upload-jobs");
        try {
            Files.createDirectory(dir);
        } catch(FileAlreadyExistsException e){
            // ignore
        }
        return dir;
    }

}

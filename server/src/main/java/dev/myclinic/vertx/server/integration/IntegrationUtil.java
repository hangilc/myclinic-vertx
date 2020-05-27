package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.IIOException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class IntegrationUtil {

    public static Path getMyclinicSpringProjectDir() {
        String dir = System.getenv("MYCLINIC_SPRING_PROJECT_DIR");
        if (dir == null) {
            throw new RuntimeException("env var not defined: " + "MYCLINIC_SPRING_PROJECT_DIR");
        }
        return Path.of(dir);
    }

    static class ExecResult {
        public int retCode;
        public byte[] stdOut;
        public byte[] stdErr;

        ExecResult(int retCode) {
            this.retCode = retCode;
        }

        boolean isError() {
            return retCode != 0;
        }

        String getErrorMessage(){
            String msg = "";
            if( stdErr != null ){
                msg = new String(stdErr, StandardCharsets.UTF_8);
            }
            return msg;
        }

    }

    public static class ExecRequest {
        public List<String> command;
        public File directory;
        public Map<String, String> env;
        public byte[] stdIn;
    }

    public static ExecResult exec(ExecRequest req) {
        ProcessBuilder pb = new ProcessBuilder(req.command);
        if (req.directory != null) {
            pb.directory(req.directory);
        }
        if (req.env != null) {
            Map<String, String> penv = pb.environment();
            for (String key : req.env.keySet()) {
                penv.put(key, req.env.get(key));
            }
        }
        try {
            Process process = pb.start();
            if( req.stdIn != null ){
                process.getOutputStream().write(req.stdIn);
            }
            InputStream os = process.getInputStream();
            InputStream es = process.getErrorStream();
            byte[] stdOut = os.readAllBytes();
            byte[] stdErr = es.readAllBytes();
            ExecResult er = new ExecResult(process.exitValue());
            er.stdOut = stdOut;
            er.stdErr = stdErr;
            return er;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}

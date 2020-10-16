package dev.myclinic.vertx.server.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.server.GlobalService;
import io.vertx.ext.web.RoutingContext;

import javax.imageio.IIOException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

class IntegrationUtil {

//    public static String getConfigDir() {
//        return GlobalService.getInstance().resolveAppPath(
//                GlobalService.getInstance().configDirToken
//        ).toString();
//    }

    public static Path getMyclinicSpringProjectDir() {
        return GlobalService.getInstance().resolveAppPath(
                GlobalService.getInstance().myclinicSpringProjectDirToken
        );
    }

    public static int getIntParam(RoutingContext ctx, String name) {
        List<String> list = ctx.queryParam(name);
        if (list.size() == 0) {
            throw new RuntimeException("Missing param: " + name);
        } else if (list.size() == 1) {
            String para = list.get(0);
            try {
                return Integer.parseInt(para);
            } catch (NumberFormatException e) {
                throw new RuntimeException(String.format("Invalid number (%s): %s", name, para));
            }
        } else {
            throw new RuntimeException("Multiple values supplied: " + name);
        }
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
                process.getOutputStream().close();
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

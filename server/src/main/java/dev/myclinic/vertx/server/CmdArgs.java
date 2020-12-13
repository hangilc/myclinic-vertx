package dev.myclinic.vertx.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class CmdArgs {

    int port = 28080;
    boolean isDev = false;
    boolean simulateSlowUpload = false;
    boolean simulateUploadFail = false;
    boolean simulateSlowDownload = false;
    List<String> args = new ArrayList<>();

    public static CmdArgs parse(String[] args) {
        CmdArgs cmdArgs = new CmdArgs();
        int i = 0;
        first:
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--port": {
                    cmdArgs.port = Integer.parseInt(args[++i]);
                    break;
                }
                case "--dev": {
                    cmdArgs.isDev = true;
                    break;
                }
                case "--simulate-slow-download": {
                    cmdArgs.simulateSlowDownload = true;
                    break;
                }
                case "--simulate-upload-fail": {
                    cmdArgs.simulateUploadFail = true;
                    break;
                }
                case "--simulate-slow-upload": {
                    cmdArgs.simulateSlowUpload = true;
                    break;
                }
                default: {
                    if (arg.startsWith("-")) {
                        System.err.println(String.format("Invalid option: %s", arg));
                        System.err.println();
                        usage();
                        System.exit(1);
                    } else {
                        break first;
                    }
                }
            }
        }
        for (; i < args.length; i++) {
            cmdArgs.args.add(args[i]);
        }
        return cmdArgs;
    }

    public static String findWebroot() {
        String s = "./server/src/main/resources/webroot";
        Path path = Path.of(s);
        if (Files.exists(path) && Files.isDirectory(path)) {
            return s;
        }
        System.out.println("Cannot find webroot.");
        System.exit(1);
        return null;
    }

    public static void usage() {
        System.err.println("Usage: server [options]");
        System.err.println("  options:");
        System.err.println("    --port PORT              server listening port");
        System.err.println("    --dev                    server files instead of resources");
        System.err.println("    --simulate-slow-upload   ");
        System.err.println("    --simulate-upload-fail   ");
        System.err.println("    --simulate-slow-download ");
    }

}

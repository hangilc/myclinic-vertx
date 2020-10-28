package dev.myclinic.vertx.drawerprinterwin;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.drawer.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrinterEnv {

    public static class SettingDirNotSuppliedException extends Exception {
    }

    private static Logger logger = LoggerFactory.getLogger(PrinterEnv.class);
    private Path settingDir;

    public PrinterEnv() {
        this(Paths.get(System.getProperty("user.home"), "myclinic-env", "printer-settings"));
    }

    public PrinterEnv(Path settingDir) {
        this.settingDir = settingDir;
    }

    public void print(List<List<Op>> pages, String settingName) {
        DrawerPrinter drawerPrinter = new DrawerPrinter();
        byte[] devmode, devnames;
        AuxSetting auxSetting;
        if (settingName == null || settingName.isEmpty()) {
            DrawerPrinter.DialogResult result = drawerPrinter.printDialog(null, null);
            if (result.ok) {
                devmode = result.devmodeData;
                devnames = result.devnamesData;
                auxSetting = null;
            } else {
                return;
            }
        } else if (settingDir == null) {
            throw new RuntimeException("Printer setting directory is not specified.");
        } else {
            if (!nameExists(settingName)) {
                throw new RuntimeException("No such setting. " + settingName);
            }
            devmode = readDevmode(settingName);
            devnames = readDevnames(settingName);
            auxSetting = readAuxSetting(settingName);
        }
        drawerPrinter.printPages(pages, devmode, devnames, auxSetting);
    }

    public void printSinglePage(List<Op> page, String setting) {
        print(Collections.singletonList(page), setting);
    }

//    public boolean createNewSetting(String name) throws IOException, SettingDirNotSuppliedException {
//        if (settingDir == null) {
//            throw new SettingDirNotSuppliedException();
//        }
//        DrawerPrinter drawerPrinter = new DrawerPrinter();
//        DrawerPrinter.DialogResult result = drawerPrinter.printDialog(null, null);
//        if( !result.ok ){
//            return false;
//        }
//        byte[] devmode = result.devmodeData;
//        byte[] devnames = result.devnamesData;
//        AuxSetting auxSetting = new AuxSetting();
//        saveSetting(name, devnames, devmode, auxSetting);
//        return true;
//    }

    public void deleteSetting(String name) {
        try {
            Files.delete(devnamesSettingPath(name));
            Files.delete(devmodeSettingPath(name));
            Files.delete(auxSettingPath(name));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void saveSetting(String name, byte[] devnames, byte[] devmode) {
        if (settingDir == null) {
            throw new RuntimeException("Printer setting directory is not specified.");
        }
        try {
            Files.write(devnamesSettingPath(name), devnames);
            Files.write(devmodeSettingPath(name), devmode);
        } catch (IOException ex) {
            try {
                Files.delete(devnamesSettingPath(name));
            } catch (Exception ex2) {
                logger.error("double exception");
            }
            throw new UncheckedIOException(ex);
        }
    }

    public void saveSetting(String name, AuxSetting auxSetting) {
        if (settingDir == null) {
            throw new RuntimeException("Printer setting directory is not specified.");
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(auxSettingPath(name).toString()), auxSetting);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void saveSetting(String name, byte[] devnames, byte[] devmode, AuxSetting auxSetting) {
        saveSetting(name, devnames, devmode);
        saveSetting(name, auxSetting);
    }

    public List<String> listNames() {
        if (settingDir == null) {
            return Collections.emptyList();
        }
        List<String> names = new ArrayList<>();
        try {
            for (Path path : Files.newDirectoryStream(settingDir, "*.devnames")) {
                String fileName = path.getFileName().toString();
                int pos = fileName.lastIndexOf('.');
                String name = fileName.substring(0, pos);
                names.add(name);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return names;
    }

    public boolean settingExists(String name) {
        return nameExists(name);
    }

    private boolean nameExists(String name) {
        return Files.exists(devnamesSettingPath(name)) &&
                Files.exists(devmodeSettingPath(name));
    }

    private Path devnamesSettingPath(String name) {
        return settingDir.resolve(name + ".devnames");
    }

    private Path devmodeSettingPath(String name) {
        return settingDir.resolve(name + ".devmode");
    }

    private Path auxSettingPath(String name) {
        return settingDir.resolve(name + ".json");
    }

    public byte[] readDevnames(String name) {
        try {
            return Files.readAllBytes(devnamesSettingPath(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] readDevmode(String name) {
        try {
            return Files.readAllBytes(devmodeSettingPath(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public AuxSetting readAuxSetting(String name) {
        Path path = auxSettingPath(name);
        if (Files.exists(path)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(new File(auxSettingPath(name).toString()), AuxSetting.class);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            return new AuxSetting();
        }
    }

}

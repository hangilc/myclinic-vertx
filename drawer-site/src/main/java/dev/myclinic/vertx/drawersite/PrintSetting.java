package dev.myclinic.vertx.drawersite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.drawerprinterwin.AuxSetting;

import java.io.IOException;
import java.util.Base64;

public class PrintSetting {

    public byte[] devmode;
    public byte[] devnames;
    public AuxSetting auxSetting;

    public PrintSetting(){

    }

    public PrintSetting(byte[] devmode, byte[] devnames) {
        this.devmode = devmode;
        this.devnames = devnames;
    }

    public PrintSetting(byte[] devmode, byte[] devnames, AuxSetting auxSetting) {
        this.devmode = devmode;
        this.devnames = devnames;
        this.auxSetting = auxSetting;
    }

    public static class Serialized {
        public String devmode;
        public String devnames;
        public AuxSetting auxSetting;
    }

    public byte[] serialize(ObjectMapper mapper) throws JsonProcessingException {
        Serialized ser = new Serialized();
        ser.devmode = Base64.getEncoder().encodeToString(this.devmode);
        ser.devnames = Base64.getEncoder().encodeToString(this.devnames);
        ser.auxSetting = this.auxSetting;
        return mapper.writeValueAsBytes(ser);
    }

    public static PrintSetting deserialize(ObjectMapper mapper, byte[] bytes) throws IOException {
        Serialized ser = mapper.readValue(bytes, Serialized.class);
        PrintSetting setting = new PrintSetting();
        setting.devmode = Base64.getDecoder().decode(ser.devmode);
        setting.devnames = Base64.getDecoder().decode(ser.devnames);
        setting.auxSetting = ser.auxSetting;
        return setting;
    }

}

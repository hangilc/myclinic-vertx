package dev.myclinic.vertx.appconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.ClinicInfoDTO;
import io.vertx.config.yaml.YamlProcessor;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Vertx;

import java.io.File;

public class FileBasedAppConfig implements AppConfig {

    private final String configDir;
    private final Vertx vertx;

    public FileBasedAppConfig(String configDir, Vertx vertx) {
        this.configDir = configDir;
        this.vertx = vertx;
    }

    @Override
    public Future<ClinicInfoDTO> getClinicInfo() {
        File file = new File(configDir, "clinic-info.yml");
        Promise<ClinicInfoDTO> promise = Promise.promise();
        vertx.fileSystem().readFile(file.toString(), ar -> {
            if( ar.failed() ){
                promise.fail(ar.cause());
            } else {
                try {
                    Buffer buffer = ar.result();
                    YamlProcessor yamlProcessor = new YamlProcessor();
                    yamlProcessor.process(vertx, null, buffer, ar2 -> {
                        if (ar2.failed()) {
                            promise.fail(ar2.cause());
                        } else {
                            try {
                                JsonObject obj = ar2.result();
                                ClinicInfoDTO dto = fromObject(obj);
                                promise.complete(dto);
                            } catch(Exception ex){
                                promise.fail(ex);
                            }
                        }
                    });
                } catch(Exception ex){
                    promise.fail(ex);
                }
            }
        });
        return promise.future();
    }

    private ClinicInfoDTO fromObject(JsonObject obj) {
        ClinicInfoDTO dto = new ClinicInfoDTO();
        dto.name = obj.getString("name");
        dto.postalCode = obj.getString("postal-code");
        dto.address = obj.getString("address");
        dto.tel = obj.getString("tel");
        dto.fax = obj.getString("fax");
        dto.todoufukencode = getAsString(obj, "todoufukencode");
        dto.tensuuhyoucode = getAsString(obj, "tensuuhyoucode");
        dto.kikancode = getAsString(obj, "kikancode");
        dto.homepage = obj.getString("homepage");
        dto.doctorName = obj.getString("doctor-name");
        return dto;
    }

    private String getAsString(JsonObject obj, String key){
        return String.valueOf(obj.getValue(key));
    }
}

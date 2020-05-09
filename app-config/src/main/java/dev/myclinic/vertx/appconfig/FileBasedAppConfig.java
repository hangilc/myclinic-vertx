package dev.myclinic.vertx.appconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.houkatsukensa.HoukatsuKensa;
import dev.myclinic.vertx.mastermap.MasterChronoMap;
import dev.myclinic.vertx.mastermap.MasterMap;
import dev.myclinic.vertx.mastermap.MasterNameMap;
import dev.myclinic.vertx.dto.ClinicInfoDTO;
import dev.myclinic.vertx.dto.DiseaseExampleDTO;
import dev.myclinic.vertx.dto.PracticeConfigDTO;
import dev.myclinic.vertx.dto.ReferItemDTO;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class FileBasedAppConfig implements AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(FileBasedAppConfig.class);

    private final String configDir;
    private final Vertx vertx;
    private final ObjectMapper yamlMapper;

    private static class ClinicInfoMixIn {
        @JsonProperty("postal-code")
        public String postalCode;
        @JsonProperty("doctor-name")
        public String doctorName;
    }

    private static class DiseaseExampleMixIn {
        @JsonProperty("adj-list")
        public List<String> adjList;
    }

    private static class PracticeConfigMixIn {
        @JsonProperty("kouhatsu-kasan")
        public String kouhatsuKasan;
    }

    public FileBasedAppConfig(String configDir, Vertx vertx) {
        this.configDir = configDir;
        this.vertx = vertx;
        this.yamlMapper = new ObjectMapper(new YAMLFactory())
                .addMixIn(ClinicInfoDTO.class, ClinicInfoMixIn.class)
                .addMixIn(DiseaseExampleDTO.class, DiseaseExampleMixIn.class)
                .addMixIn(PracticeConfigDTO.class, PracticeConfigMixIn.class);
    }

    @Override
    public Future<ClinicInfoDTO> getClinicInfo() {
        File file = new File(configDir, "clinic-info.yml");
        return fromYamlFile(file, new TypeReference<>(){});
    }

    @Override
    public Future<List<DiseaseExampleDTO>> listDiseaseExample() {
        File file = new File(configDir, "disease-example.yml");
        return fromYamlFile(file, new TypeReference<>() {});
    }

    @Override
    public Future<String> getPaperScanDirectory() {
        File file = new File(configDir, "app-config.yml");
        Promise<String> promise = Promise.promise();
        vertx.executeBlocking(
            promise2 -> {
                try {
                    JsonNode node = yamlMapper.readTree(file);
                    String value = node.get("paper-scan-directory").asText();
                    promise2.complete(value);
                } catch(Exception e){
                    logger.error("Failed to get data from app-config.yml", e);
                    promise2.fail(e);
                }
            },
            promise
        );
        return promise.future();
    }

    @Override
    public Future<String> getMasterMapConfigFilePath() {
        File file = new File(configDir, "master-map.txt");
        return Future.succeededFuture(file.getAbsolutePath());
    }

    @Override
    public Future<String> getShinryouByoumeiMapConfigFilePath() {
        File file = new File(configDir, "shinryou-byoumei.yml");
        return Future.succeededFuture(file.getAbsolutePath());
    }

    @Override
    public Future<List<ReferItemDTO>> getReferList() {
        File file = new File(configDir, "refer-list.yml");
        return fromYamlFile(file, new TypeReference<>(){});
    }

    @Override
    public Future<String> getNameMapConfigFilePath() {
        File file = new File(configDir, "master-name.txt");
        return Future.succeededFuture(file.getAbsolutePath());
    }

    @Override
    public Future<String> getPowderDrugConfigFilePath() {
        File file = new File(configDir, "powder-drug.txt");
        return Future.succeededFuture(file.getAbsolutePath());
    }

    @Override
    public Future<PracticeConfigDTO> getPracticeConfig() {
        File file = new File(configDir, "practice-config.yml");
        return fromYamlFile(file, new TypeReference<>(){});
    }

    @Override
    public MasterMap getMasterMap() {
        File nameMapFile = new File(configDir, "master-name.txt");
        File chronoMapFile = new File(configDir, "master-map.txt");
        try {
            MasterNameMap nameMap = MasterNameMap.fromFile(nameMapFile);
            MasterChronoMap chronoMap = MasterChronoMap.fromFile(chronoMapFile);
            return new MasterMap(nameMap, chronoMap);
        } catch(Exception e){
            throw new RuntimeException("Failed to read master map.", e);
        }
    }

    @Override
    public HoukatsuKensa getHoukatsuKensa() {
        File file = new File(configDir, "houkatsu-kensa.xml");
        try {
            return HoukatsuKensa.fromXmlFile(file);
        } catch(Exception e){
            throw new RuntimeException("Failed to read houkatsu-kensa from: " + file.toString());
        }
    }

    private <T> Future<T> fromYamlFile(File file, TypeReference<T> typeRef){
        Promise<T> promise = Promise.promise();
        vertx.executeBlocking(
            promise2 -> {
                try {
                    T dto = yamlMapper.readValue(file, typeRef);
                    promise2.complete(dto);
                } catch(Exception ex){
                    logger.error("Failed to read YAML file.", ex);
                    promise2.fail(ex);
                }
            },
            promise
        );
        return promise.future();
    }

}

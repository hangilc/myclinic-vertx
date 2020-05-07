package dev.myclinic.vertx.server;

import dev.myclinic.vertx.dto.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class NoDatabaseImpl {

    public List<ReferItemDTO> getReferList() throws Exception {
        throw new RuntimeException("getReferList");
    }

    public StringResultDTO getNameMapConfigFilePath() throws Exception {
        throw new RuntimeException("Not implemented: getNameMapConfigFilePath");
    }

    public StringResultDTO getPowderDrugConfigFilePath() throws Exception {
        throw new RuntimeException("Not implemented: getPowderDrugConfigFilePath");
    }

    public PracticeConfigDTO getPracticeConfig() throws Exception {
        throw new RuntimeException("getPracticeConfig");
    }

}

package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.client.Service;
import dev.myclinic.vertx.dto.DiseaseNewDTO;
import dev.myclinic.vertx.dto.ShinryouDTO;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

class FixerService implements Fixer {

    //private static Logger logger = LoggerFactory.getLogger(FixerService.class);
    private Service.ServerAPI api;

    FixerService(Service.ServerAPI api) {
        this.api = api;
    }

    @Override
    public int enterShinryou(ShinryouDTO shinryou) {
        try {
            return api.enterShinryouCall(shinryou).execute().body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean batchDeleteShinryou(List<Integer> shinryouIds) {
        try {
            return api.batchDeleteShinryouCall(shinryouIds).execute().body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int enterDisease(DiseaseNewDTO disease) {
        try {
            return api.enterDiseaseCall(disease).execute().body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.mastermap.MasterMap;

import java.io.IOException;

class RestHandlerBase {

    protected final ObjectMapper mapper;
    protected final MasterMap masterMap;

    RestHandlerBase(ObjectMapper mapper, MasterMap masterMap){
        this.mapper = mapper;
        this.masterMap = masterMap;
    }

    protected String jsonEncode(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    protected <T> T _convertParam(byte[] bytes, TypeReference<T> typeRef) throws IOException {
        return mapper.readValue(bytes, typeRef);
    }

}

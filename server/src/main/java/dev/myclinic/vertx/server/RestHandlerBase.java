package dev.myclinic.vertx.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class RestHandlerBase {

    protected final ObjectMapper mapper;

    RestHandlerBase(ObjectMapper mapper){
        this.mapper = mapper;
    }

    protected String jsonEncode(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    protected <T> T _convertParam(String src, TypeReference<T> typeRef) throws JsonProcessingException {
        return mapper.readValue(src, typeRef);
    }

}

package dev.myclinic.vertx.houkatsukensa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import dev.myclinic.vertx.consts.HoukatsuKensaKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JacksonXmlRootElement
public class HoukatsuKensa {
    private static final Logger logger = LoggerFactory.getLogger(HoukatsuKensa.class);

    private final static XmlMapper mapper = new XmlMapper();

    public static class LocalDateDeserializer extends StdDeserializer<LocalDate> {

        protected LocalDateDeserializer(){
            super(LocalDate.class);
        }

        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return LocalDate.parse(jsonParser.readValueAs(String.class));
        }
    }

    public static class Step {
        public int threshold;
        public int point;
    }

    public static class Group {
        public String key;
        @JsonProperty("step")
        public List<Step> steps = new ArrayList<>();

        @JsonSetter
        public void setStep(Step step){
            steps.add(step);
        }
    }

    public static class Revision {
        @JsonProperty("valid-from")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        public LocalDate validFrom;
        public List<Group> groups;

        private List<Step> getSteps(HoukatsuKensaKind kind){
            String code = kind.getCode();
            for(Group g: groups){
                if( g.key.equals(code) ){
                    return g.steps;
                }
            }
            return Collections.emptyList();
        }

        public Optional<Integer> calcTen(HoukatsuKensaKind kind, int n){
            for(Step step: getSteps(kind)){
                if( step.threshold <= n ){
                    return Optional.of(step.point);
                }
            }
            return Optional.empty();
        }
    }

    public List<Revision> revisions;

    public static HoukatsuKensa fromXmlFile(File file) throws IOException {
        try {
            return mapper.readValue(file, new TypeReference<>() {
            });
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<Integer> calcTen(HoukatsuKensaKind kind, int n, LocalDate at){
        Revision r = findRevision(at);
        if( r == null ){
            return Optional.empty();
        } else {
            return r.calcTen(kind, n);
        }
    }

    public Revision findRevision(LocalDate at){
        for(Revision r: revisions){
            if( r.validFrom.compareTo(at) <= 0 ){
                return r;
            }
        }
        return null;
    }

}

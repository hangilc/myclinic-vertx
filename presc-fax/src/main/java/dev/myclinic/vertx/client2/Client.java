package dev.myclinic.vertx.client2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.HokenDTO;
import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.dto.TextDTO;
import dev.myclinic.vertx.dto.VisitDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class Client {

    private final String serviceUrl;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public Client(String serviceUrl) {
        this.serviceUrl = serviceUrl.replaceAll("/$", "");
        this.httpClient = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        this.mapper = mapper;
    }

    protected String param(String key, String value){
        return String.format("%s=%s", URLEncoder.encode(key, StandardCharsets.UTF_8),
                URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    protected String param(String key, LocalDate value){
        return String.format("%s=%s", URLEncoder.encode(key, StandardCharsets.UTF_8),
                URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
    }

    protected String param(String key, int value){
        return String.format("%s=%d", URLEncoder.encode(key, StandardCharsets.UTF_8), value);
    }

    protected URI createURI(String path, String... params){
        if( params.length == 0 ) {
            return URI.create(serviceUrl + path);
        } else {
            return URI.create(serviceUrl + path + "?" + String.join("&", params));
        }
    }

    protected <T> T get(Class<T> cls, String path, String... params) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(createURI(path, params))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), cls);
    }

    protected <T> T get(TypeReference<T> typeRef, String path, String... params) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(createURI(path, params))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), typeRef);
    }

    // Patient //////////////////////////////////////////////////////////////////////////////////

    public PatientDTO getPatient(int patientId) throws IOException, InterruptedException {
        return get(PatientDTO.class, "/get-patient", param("patient-id", patientId));
    }

    // Visit ////////////////////////////////////////////////////////////////////////////////////

    public VisitDTO getVisit(int visitId) throws IOException, InterruptedException {
        return get(VisitDTO.class, "/get-visit", param("visit-id", visitId));
    }

    public List<Integer> listVisitIdInDateInterval(LocalDate from, LocalDate upto) throws IOException, InterruptedException {
        return get(new TypeReference<>(){}, "/list-visit-id-in-date-in-range",
                param("from", from), param("upto", upto));
    }

    // Text //////////////////////////////////////////////////////////////////////////////////////

    public List<TextDTO> listText(int visitId) throws IOException, InterruptedException {
        return get(new TypeReference<>(){}, "/list-text", param("visit-id", visitId));
    }

    // Hoken //////////////////////////////////////////////////////////////////////////////////////

    public HokenDTO getHoken(int visitId) throws IOException, InterruptedException {
        return get(HokenDTO.class, "/get-hoken", param("visit-id", visitId));
    }

}
package dev.myclinic.vertx.client2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Client {

    private final String serviceUrl;
    private final HttpClient httpClient;
    private final static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
    }

    public Client(String serviceUrl) {
        this.serviceUrl = serviceUrl.replaceAll("/$", "");
        this.httpClient = HttpClient.newHttpClient();
    }

    public static String param(String key, String value) {
        return String.format("%s=%s", URLEncoder.encode(key, StandardCharsets.UTF_8),
                URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    public static String param(String key, LocalDate value) {
        return String.format("%s=%s", URLEncoder.encode(key, StandardCharsets.UTF_8),
                URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
    }

    public static String param(String key, int value) {
        return String.format("%s=%d", URLEncoder.encode(key, StandardCharsets.UTF_8), value);
    }

    private static URI doCreateURI(String serverPath, String... params){
        if (params.length == 0) {
            return URI.create(serverPath);
        } else {
            return URI.create(serverPath + "?" + String.join("&", params));
        }
    }

    protected URI createURI(String path, String... params) {
        return doCreateURI(serviceUrl + path, params);
    }

    private HttpRequest createGetReq(String path, String[] params) {
        return HttpRequest.newBuilder()
                .uri(createURI(path, params))
                .GET()
                .build();
    }

    protected <T> T get(Class<T> cls, String path, String... params) {
        HttpRequest req = createGetReq(path, params);
        try {
            HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), cls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T get(TypeReference<T> typeRef, String path, String... params) {
        HttpRequest req = createGetReq(path, params);
        try {
            HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), typeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getAlt(TypeReference<T> typeRef, String url, String... params)
            throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(doCreateURI(url, params))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), typeRef);
    }

    public static <T> T postAlt(Class<T> cls, String url, byte[] body, String... params)
            throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .build();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(doCreateURI(url, params))
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
        HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), cls);
    }

    // Patient //////////////////////////////////////////////////////////////////////////////////

    public PatientDTO getPatient(int patientId) {
        return get(PatientDTO.class, "/get-patient", param("patient-id", patientId));
    }

    // Visit ////////////////////////////////////////////////////////////////////////////////////

    public VisitDTO getVisit(int visitId) {
        return get(VisitDTO.class, "/get-visit", param("visit-id", visitId));
    }

    public List<Integer> listVisitIdInDateInterval(LocalDate from, LocalDate upto) {
        return get(new TypeReference<>() {
                   }, "/list-visit-id-in-date-in-range",
                param("from", from), param("upto", upto));
    }

    // Text //////////////////////////////////////////////////////////////////////////////////////

    public List<TextDTO> listText(int visitId) {
        return get(new TypeReference<>() {
        }, "/list-text", param("visit-id", visitId));
    }

    // Hoken //////////////////////////////////////////////////////////////////////////////////////

    public HokenDTO getHoken(int visitId) {
        return get(HokenDTO.class, "/get-hoken", param("visit-id", visitId));
    }

    // Misc ///////////////////////////////////////////////////////////////////////////////////////

    public ClinicInfoDTO getClinicInfo() {
        return get(ClinicInfoDTO.class, "/get-clinic-info");
    }

}

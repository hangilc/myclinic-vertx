package dev.myclinic.vertx.prescfax;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class Data {

    public static Data create(LocalDate from, LocalDate upto) throws IOException, InterruptedException {
        String serviceUrl = System.getenv("MYCLINIC_SERVICE");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl + "/get-patient" + "?patient-id=198"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return null;
    }
}

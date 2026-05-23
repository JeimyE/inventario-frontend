package com.pos.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpUtil {

    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static HttpRequest.Builder builder(String endpoint) {
        var b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json");
        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            b.header("Authorization", "Bearer " + token);
        }
        return b;
    }

    public static String get(String endpoint) throws Exception {
        HttpResponse<String> res = CLIENT.send(
                builder(endpoint).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        checkStatus(res.statusCode(), res.body());
        return res.body();
    }

    public static String post(String endpoint, String body) throws Exception {
        HttpResponse<String> res = CLIENT.send(
                builder(endpoint).POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
        checkStatus(res.statusCode(), res.body());
        return res.body();
    }

    public static String put(String endpoint, String body) throws Exception {
        HttpResponse<String> res = CLIENT.send(
                builder(endpoint).PUT(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
        checkStatus(res.statusCode(), res.body());
        return res.body();
    }

    public static void delete(String endpoint) throws Exception {
        HttpResponse<String> res = CLIENT.send(
                builder(endpoint).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
        checkStatus(res.statusCode(), res.body());
    }

    public static byte[] downloadBytes(String endpoint) throws Exception {
        HttpResponse<byte[]> res = CLIENT.send(
                builder(endpoint).GET().build(),
                HttpResponse.BodyHandlers.ofByteArray());
        if (res.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + res.statusCode());
        }
        return res.body();
    }

    private static void checkStatus(int status, String body) {
        if (status >= 400) {
            throw new RuntimeException("HTTP " + status + ": " + body);
        }
    }
}

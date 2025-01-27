package com.bot.takagi3.util;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpUtil
{
    private HttpUtil(){}

    public static String asyncPost(String url, String jsonStr, int timeout)
    {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonStr))
                .build();

        CompletableFuture<String> result = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    log.error("Request to {} failed: {}", url, e.getMessage());
                    return null;
                });

        // 等待异步请求完成并获取结果
        return result.join();
    }
    public static String asyncPost(String url, String jsonStr, String auth, int timeout)
    {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeout))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", auth)
                .POST(HttpRequest.BodyPublishers.ofString(jsonStr))
                .build();


        CompletableFuture<String> result = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    log.error("Request to {} failed: {}", url, e.getMessage());
                    return null;
                });

        // 等待异步请求完成并获取结果
        return result.join();
    }
}

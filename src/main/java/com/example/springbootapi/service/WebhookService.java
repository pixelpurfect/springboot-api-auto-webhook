package com.example.springbootapi.service;

import com.example.springbootapi.model.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebClient.Builder webClientBuilder;

    @PostConstruct
    public void init() {
        RequestPayload requestPayload = new RequestPayload();
        requestPayload.setName("Lakshaya Krishnaraj");
        requestPayload.setRegNo("RA2211026010342");
        requestPayload.setEmail("lk6482@srmist.edu.in");

        WebClient webClient = webClientBuilder.build();

        webClient.post()
                .uri("https
::contentReference[oaicite:0]{index=0}
                 .uri("https://webhook.site/your-custom-path/generateWebhook") // Replace with actual URL
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(ResponseData.class)
                .doOnError(error -> log.error("Failed to register: {}", error.getMessage()))
                .subscribe(responseData -> {
                    log.info("Successfully registered and received webhook: {}", responseData.getWebhook());
                    processDataAndSend(responseData);
                });
    }

    private void processDataAndSend(ResponseData responseData) {
        int findId = responseData.getData().getFindId();
        int n = responseData.getData().getN();
        List<User> users = responseData.getData().getUsers();

        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (User user : users) {
            graph.put(user.getId(), user.getFollows() != null ? user.getFollows() : new ArrayList<>());
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(findId);
        visited.add(findId);

        int level = 0;
        while (!queue.isEmpty() && level < n) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int current = queue.poll();
                for (int neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                    if (!visited.contains(neighbor)) {
                        queue.offer(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
            level++;
        }

        List<Integer> nthLevelFollowers = new ArrayList<>(queue);
        Collections.sort(nthLevelFollowers);

        OutcomePayload outcome = new OutcomePayload();
        outcome.setRegNo("RA2211026010342");
        outcome.setOutcome(nthLevelFollowers);

        sendToWebhook(responseData.getWebhook(), responseData.getAccessToken(), outcome, 0);
    }

    private void sendToWebhook(String webhookUrl, String token, OutcomePayload outcome, int retryCount) {
        if (retryCount >= 4) {
            log.error("Max retries reached. Failed to send data.");
            return;
        }

        webClientBuilder.build().post()
                .uri(webhookUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(outcome)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> log.info("Data sent successfully to webhook!"))
                .doOnError(error -> {
                    log.warn("Attempt {} failed: {}", retryCount + 1, error.getMessage());
                    sendToWebhook(webhookUrl, token, outcome, retryCount + 1);
                })
                .subscribe();
    }
}


package com.example.springbootapi.model;

import lombok.Data;
import java.util.List;

@Data
public class ResponseData {
    private String webhook;
    private String accessToken;
    private DataPayload data;

    @Data
    public static class DataPayload {
        private int n;
        private int findId;
        private List<User> users;
    }
}

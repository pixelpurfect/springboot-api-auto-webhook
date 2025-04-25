package com.example.springbootapi.model;

import lombok.Data;
import java.util.List;

@Data
public class OutcomePayload {
    private String regNo;
    private List<Integer> outcome;
}

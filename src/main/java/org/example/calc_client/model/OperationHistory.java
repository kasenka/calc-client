package org.example.calc_client.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationHistory {
    private String id;
    private String username;
    private String action;
    private String values;
    private String result;
}

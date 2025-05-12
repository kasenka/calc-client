package org.example.calc_client;

import java.util.List;

public class ApiErrorResponse {
    private String error;          // для единичной ошибки
    private List<String> errors;  // для множества ошибок

    // геттеры и сеттеры
    public String getError() {
        return error;
    }

    public List<String> getErrors() {
        return errors;
    }
}

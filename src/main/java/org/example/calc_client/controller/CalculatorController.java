package org.example.calc_client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.example.calc_client.CurrencyRateDTO;
import org.example.calc_client.Session;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalculatorController {
    private final String API_BASE_URL = "http://localhost:8080/api/calculator";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML private ComboBox<String> fromCurrencyCombo;
    @FXML private ComboBox<String> toCurrencyCombo;
    @FXML private TextField amountField;
    @FXML private Label resultLabel;
    @FXML private TextField voltageField;
    @FXML private TextField currentField;
    @FXML private TextField resistanceField;
    @FXML private Label errorLabel;
    @FXML private BorderPane calcContainer;

    @FXML
    public void initialize() {
        HttpEntity<String> request = new HttpEntity<>(
                Session.getJwtHeader()
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                API_BASE_URL ,
                HttpMethod.GET,
                request,
                Map.class
        );

        List<Map<String, Object>> body = (List<Map<String, Object>>) response.getBody().get("currencies");

        List<CurrencyRateDTO> currencies = body.stream()
                .map(currencyMap -> {
                    try {
                        return objectMapper.convertValue(currencyMap, CurrencyRateDTO.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());

        fromCurrencyCombo.setItems(currencies.stream()
                .map(c -> c.getFromCurrency())
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        toCurrencyCombo.setItems(currencies.stream()
                .map(c -> c.getToCurrency())
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        fromCurrencyCombo.setValue("USD");
        toCurrencyCombo.setValue("EUR");
    }

    @FXML
    public void handleConvert() {

    }

    @FXML
    public void handleCalculate() {

    }
    @FXML
    public void handleHistory() {

    }
}

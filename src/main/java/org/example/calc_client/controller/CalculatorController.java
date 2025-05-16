package org.example.calc_client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.calc_client.model.CurrencyRateDTO;
import org.example.calc_client.model.Session;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CalculatorController {
    private final String API_BASE_URL = "http://localhost:8080/api/calculator";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<CurrencyRateDTO> currencyRatesDTO = new ArrayList<CurrencyRateDTO>();
    private List<Map<String, Object>> users;

    @FXML private ComboBox<String> fromCurrencyCombo;
    @FXML private ComboBox<String> toCurrencyCombo;
    @FXML private TextField amountField;
    @FXML private Label curResultLabel;
    @FXML private TextField voltageField;
    @FXML private TextField currentField;
    @FXML private TextField resistanceField;
    @FXML private BorderPane calcContainer;
    @FXML private Label curErrorLabel;
    @FXML private Label ohmsErrorLabel;
    @FXML private Label ohmsResultLabel;
    @FXML private Button withRole;
    @FXML private ComboBox<String> deleteUser;
    @FXML private Label delErrorLabel;
    @FXML private Label successMessage;
    @FXML private VBox delete;


    @FXML
    public void initialize() {

        if (Session.getRole().equals("ADMIN")) {
            withRole.setVisible(true);
            delete.setVisible(true);

            HttpEntity<String> getRequest = new HttpEntity<>(
                    Session.getJwtHeader()
            );

            ResponseEntity<Map> getResponse = restTemplate.exchange(
                    API_BASE_URL + "/admin/getUsers",
                    HttpMethod.GET,
                    getRequest,
                    Map.class
            );

            users = (List<Map<String, Object>>) getResponse.getBody().get("users");
            deleteUser.setItems(users.stream()
                    .map(m -> (String) m.get("username"))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }



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

        currencyRatesDTO = currencies;

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
    public void handleConvert() throws Exception {

        if (amountField.getText().isEmpty()) {
            curErrorLabel.setText("Заполните все поля");
            curErrorLabel.setVisible(true);
            return;
        }

        Double amount = Double.valueOf(amountField.getText());
        String fromCurrency = fromCurrencyCombo.getValue();
        String toCurrency = toCurrencyCombo.getValue();


        long currencyId = currencyRatesDTO.stream()
                .filter(c -> c.getFromCurrency().equals(fromCurrency)
                && c.getToCurrency().equals(toCurrency)).findFirst().get().getId();

        try{

            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(Map.of(
                            "id", String.valueOf(currencyId),
                            "amount", String.valueOf(amount)
                    )),
                    Session.getJwtHeader()
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_BASE_URL + "/currency",
                    request,
                    Map.class
            );


            if (response.getStatusCode() == HttpStatus.OK) {
                curResultLabel.setText(String.valueOf(response.getBody().get("result")));
                curResultLabel.setVisible(true);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                curErrorLabel.setText(AuthController.processErrorResponse(e));
                curErrorLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCalculate() throws Exception {
        Double voltage = voltageField.getText().isBlank() ? null : Double.parseDouble(voltageField.getText());
        Double resistance = resistanceField.getText().isBlank() ? null : Double.parseDouble(resistanceField.getText());
        Double current = currentField.getText().isBlank() ? null : Double.parseDouble(currentField.getText());

        try{
            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(Map.of(
                            "voltage", String.valueOf(voltage),
                            "current", String.valueOf(current),
                            "resistance", String.valueOf(resistance)
                    )),
                    Session.getJwtHeader()
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_BASE_URL + "/ohms-law",
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ohmsResultLabel.setText(String.valueOf(response.getBody().get("result")));
                ohmsResultLabel.setVisible(true);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                ohmsErrorLabel.setText(AuthController.processErrorResponse(e));
                ohmsErrorLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleHistory() throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/calc_client/views/showHistory.fxml"));
            Parent view = loader.load();

            HistoryController controller = loader.getController();

            // Теперь заменить текущую сцену:
            Scene scene = new Scene(view);
            Stage stage = (Stage) calcContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() throws Exception {

        Optional<Map<String, Object>> user = users.stream()
                .filter(m -> String.valueOf(m.get("username")).equals(deleteUser.getValue()))
                .findFirst();

        long id = Long.parseLong(user.get().get("id").toString());

        try{
            HttpEntity<String> request = new HttpEntity<>(
                    Session.getJwtHeader()
            );

            ResponseEntity<Map> response = restTemplate.exchange(
                    API_BASE_URL + "/admin/" + id + "/delete",
                    HttpMethod.DELETE,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                successMessage.setText("Успешное удаление");
                successMessage.setVisible(true);
                delErrorLabel.setVisible(false);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                delErrorLabel.setText(AuthController.processErrorResponse(e));
                delErrorLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNewCurrency() throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/calc_client/views/createCurrency.fxml"));
            Parent view = loader.load();

            CreateCurrencyController controller = loader.getController();

            Scene scene = new Scene(view);
            Stage stage = (Stage) calcContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

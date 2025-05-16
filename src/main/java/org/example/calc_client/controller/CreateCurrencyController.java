package org.example.calc_client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.calc_client.model.Session;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

public class CreateCurrencyController {
    private final String API_BASE_URL = "http://localhost:8080/api/calculator/admin/createRate";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private TextField rateField;

    @FXML private Label successMessage;
    @FXML private Label createErrorLabel;

    @FXML private BorderPane createCurrencyContainer;


    @FXML
    public void handleSave() throws Exception{
        try{
            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(Map.of(
                            "fromCurrency", fromField.getText(),
                            "toCurrency", toField.getText(),
                            "rate", rateField.getText()
                    )),
                    Session.getJwtHeader()
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_BASE_URL,
                    request,
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.CREATED) {
                successMessage.setText("Успешное создание");
                successMessage.setVisible(true);
                createErrorLabel.setVisible(false);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                createErrorLabel.setText(AuthController.processErrorResponse(e));
                createErrorLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToCalc() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/calc_client/views/calc.fxml"));
            Parent view = loader.load();

            CalculatorController controller = loader.getController();

            // Теперь заменить текущую сцену:
            Scene scene = new Scene(view);
            Stage stage = (Stage) createCurrencyContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

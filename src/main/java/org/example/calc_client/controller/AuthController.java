package org.example.calc_client.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.calc_client.ApiErrorResponse;
import org.example.calc_client.Session;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthController {
    @FXML
    private BorderPane loginContainer;

    @FXML
    private BorderPane calcContainer;


    private final String API_BASE_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Login fields
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginErrorLabel;

    // Register fields
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private Label regErrorLabel;

    private String processErrorResponse(HttpClientErrorException e) throws Exception {

        String responseBody = e.getResponseBodyAsString();
        JsonNode result = objectMapper.readTree(responseBody);

        if (result.has("error")) {
            String error = result.get("error").asText();
            return error;
        }
        else{
            // Формат: {"errors": ["Сообщение1", "Сообщение2"]}
            List<String> errors = new ArrayList<>();
            for (JsonNode errorNode : result.get("errors")) {
                errors.add(errorNode.asText());
            }
            return String.join("\n", errors);
        }
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Заполните все поля");
            loginErrorLabel.setVisible(true);
            return;
        }

        try {
            Map<String, String> request = Map.of(
                    "username", username,
                    "password", password
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_BASE_URL + "/login",
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String jwtToken = (String) response.getBody().get("jwt");
                Session.setJwt(jwtToken);

                System.out.println("Login successful! JWT: " + jwtToken);

                handleLoginSuccess();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                try{
                    String error = processErrorResponse(e);
                    showRegisterError(error);
                }catch (Exception ex){
                    e.printStackTrace();
                }
            } else {
                showLoginError("Ошибка сервера: " + e.getStatusCode());
            }
        } catch (Exception e) {
            showLoginError("Ошибка соединения с сервером");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        String username = regUsernameField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = regConfirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            regErrorLabel.setText("Заполните все поля");
            regErrorLabel.setVisible(true);
            return;
        }

        try{
            Map<String, String> request = Map.of(
                    "username", username,
                    "password", password
            );


            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_BASE_URL + "/register",
                    request,
                    Map.class
            );


            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Registration successful!");
                regErrorLabel.setText("Регистрация успешна! Теперь вы можете войти.");
                regErrorLabel.setStyle("-fx-text-fill: green;");
                regErrorLabel.setVisible(true);

                regUsernameField.clear();
                regPasswordField.clear();
                regConfirmPasswordField.clear();
            }
        }catch (HttpClientErrorException e){
             if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                try{
                    String error = processErrorResponse(e);
                    showRegisterError(error);
                }catch (Exception ex){
                    e.printStackTrace();
                }
            } else {
                showRegisterError("Ошибка сервера: " + e.getStatusCode());
            }
        } catch (Exception e) {
            showLoginError("Ошибка соединения с сервером");
            e.printStackTrace();
        }
    }

    private void showLoginError(String message) {
        loginErrorLabel.setText(message);
        loginErrorLabel.setStyle("-fx-text-fill: red;");
        loginErrorLabel.setVisible(true);
    }

    private void showRegisterError(String message) {
        regErrorLabel.setText(message);
        regErrorLabel.setStyle("-fx-text-fill: red;");
        regErrorLabel.setVisible(true);
    }

    @FXML
    private void handleLoginSuccess() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/calc_client/views/calc.fxml"));
            Parent view = loader.load();

            CalculatorController controller = loader.getController();

            // Теперь заменить текущую сцену:
            Scene scene = new Scene(view);
            Stage stage = (Stage) loginContainer.getScene().getWindow(); // loginContainer — например, BorderPane
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

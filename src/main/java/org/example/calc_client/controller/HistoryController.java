package org.example.calc_client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.calc_client.model.OperationHistory;
import org.example.calc_client.model.Session;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HistoryController {
    private final String API_BASE_URL = "http://localhost:8080/api/calculator";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private TableView<OperationHistory> historyTable;
    @FXML private TableColumn<OperationHistory, LocalDateTime> nameColumn;
    @FXML private TableColumn<OperationHistory, String> typeColumn;
    @FXML private TableColumn<OperationHistory, String> detailsColumn;
    @FXML private TableColumn<OperationHistory, String> resultColumn;
//    @FXML private Label statusLabel;

    private ObservableList<OperationHistory> historyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("values"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));

        refreshData();

    }

    @FXML
    private void refreshData() {
        historyData.clear();
        historyData.addAll(loadHistoryFromDatabase());
        historyTable.setItems(historyData);
    }

    private List<OperationHistory> loadHistoryFromDatabase() {

        try{
            HttpEntity<String> request = new HttpEntity<>(
                    Session.getJwtHeader()
            );

            String url = (Session.getRole().equals("USER"))? "/user/history" : "/admin/usersHistory";


            ResponseEntity<Map> response = restTemplate.exchange(
                    API_BASE_URL + url,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Object historyObj = response.getBody().get("history");

                List<OperationHistory> history = objectMapper.convertValue(
                        historyObj,
                        new TypeReference<List<OperationHistory>>() {}
                );
                return history;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @FXML
    private void backToCalc() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/calc_client/views/calc.fxml"));
            Parent view = loader.load();

            CalculatorController controller = loader.getController();

            // Теперь заменить текущую сцену:
            Scene scene = new Scene(view);
            Stage stage = (Stage) historyTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

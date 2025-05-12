package org.example.calc_client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private BorderPane mainContainer;

    @FXML
    private void showLogin() throws IOException {
        loadView("/org/example/calc_client/views/login.fxml");
    }

    @FXML
    private void showRegister() throws IOException {
        loadView("/org/example/calc_client/views/register.fxml");
    }


    private void loadView(String fxmlPath) throws IOException {
        try {
            // Получаем URL ресурса
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            // Загружаем FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();
            mainContainer.setCenter(view);

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
            throw e;
        }
    }
}

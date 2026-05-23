package com.pos.controllers;

import com.pos.MainApp;
import com.pos.services.AuthService;
import com.pos.utils.AlertUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required.");
            return;
        }

        errorLabel.setText("Authenticating...");
        usernameField.setDisable(true);
        passwordField.setDisable(true);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return authService.login(username, password);
            }
        };

        task.setOnSucceeded(e -> {
            if (Boolean.TRUE.equals(task.getValue())) {
                try {
                    MainApp.showMainScreen();
                } catch (Exception ex) {
                    AlertUtil.showError("Navigation Error", ex.getMessage());
                }
            } else {
                errorLabel.setText("Invalid credentials.");
                resetFields();
            }
        });

        task.setOnFailed(e -> {
            errorLabel.setText("Connection error. Is the server running?");
            resetFields();
        });

        new Thread(task).start();
    }

    private void resetFields() {
        usernameField.setDisable(false);
        passwordField.setDisable(false);
        passwordField.clear();
    }
}

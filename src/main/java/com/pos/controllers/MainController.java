package com.pos.controllers;

import com.pos.MainApp;
import com.pos.utils.AlertUtil;
import com.pos.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private StackPane contentPane;
    @FXML private Button dashboardBtn;
    @FXML private Button productsBtn;
    @FXML private Button clientsBtn;
    @FXML private Button cashiersBtn;
    @FXML private Button categoriesBtn;
    @FXML private Button invoicesBtn;
    @FXML private Button statisticsBtn;

    private Button activeButton;

    @FXML
    public void initialize() {
        showDashboard();
    }

    private void loadView(String fxmlFile, Button clickedBtn) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/pos/fxml/" + fxmlFile));
            Node view = loader.load();
            contentPane.getChildren().setAll(view);

            if (activeButton != null) {
                activeButton.getStyleClass().remove("nav-btn-active");
            }
            clickedBtn.getStyleClass().add("nav-btn-active");
            activeButton = clickedBtn;
        } catch (Exception e) {
            AlertUtil.showError("Navigation Error", "Could not load view: " + e.getMessage());
        }
    }

    @FXML private void showDashboard()   { loadView("dashboard.fxml",   dashboardBtn); }
    @FXML private void showProducts()    { loadView("products.fxml",    productsBtn); }
    @FXML private void showClients()     { loadView("clients.fxml",     clientsBtn); }
    @FXML private void showCashiers()    { loadView("cashiers.fxml",    cashiersBtn); }
    @FXML private void showCategories()  { loadView("categories.fxml",  categoriesBtn); }
    @FXML private void showInvoices()    { loadView("invoices.fxml",    invoicesBtn); }
    @FXML private void showStatistics()  { loadView("statistics.fxml",  statisticsBtn); }

    @FXML
    private void handleLogout() {
        if (AlertUtil.showConfirm("Logout", "Are you sure you want to log out?")) {
            SessionManager.getInstance().clearSession();
            try {
                MainApp.showLoginScreen();
            } catch (Exception ex) {
                AlertUtil.showError("Error", ex.getMessage());
            }
        }
    }
}

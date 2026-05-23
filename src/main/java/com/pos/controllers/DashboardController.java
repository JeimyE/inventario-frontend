package com.pos.controllers;

import com.pos.models.DashboardStats;
import com.pos.services.StatisticsService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label totalProductosLabel;
    @FXML private Label totalClientesLabel;
    @FXML private Label totalFacturasLabel;
    @FXML private Label ingresosLabel;

    private final StatisticsService statisticsService = new StatisticsService();

    @FXML
    public void initialize() {
        loadStats();
    }

    private void loadStats() {
        Task<DashboardStats> task = new Task<>() {
            @Override
            protected DashboardStats call() throws Exception {
                return statisticsService.getResumen();
            }
        };

        task.setOnSucceeded(e -> {
            DashboardStats s = task.getValue();
            if (s != null) {
                totalProductosLabel.setText(String.valueOf(s.getTotalProductos() != null ? s.getTotalProductos() : 0));
                totalClientesLabel.setText(String.valueOf(s.getTotalClientes() != null ? s.getTotalClientes() : 0));
                totalFacturasLabel.setText(String.valueOf(s.getTotalFacturas() != null ? s.getTotalFacturas() : 0));
                ingresosLabel.setText(String.format("$%.2f", s.getIngresos() != null ? s.getIngresos() : 0.0));
            }
        });

        task.setOnFailed(e -> Platform.runLater(() -> {
            totalProductosLabel.setText("—");
            totalClientesLabel.setText("—");
            totalFacturasLabel.setText("—");
            ingresosLabel.setText("—");
        }));

        new Thread(task).start();
    }
}

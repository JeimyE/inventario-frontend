package com.pos.controllers;

import com.pos.services.StatisticsService;
import com.pos.utils.AlertUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML private LineChart<String, Number> salesChart;
    @FXML private CategoryAxis salesXAxis;
    @FXML private NumberAxis salesYAxis;

    @FXML private BarChart<String, Number> topProductsChart;
    @FXML private CategoryAxis topXAxis;
    @FXML private NumberAxis topYAxis;

    private final StatisticsService statisticsService = new StatisticsService();

    @FXML
    public void initialize() {
        salesChart.setAnimated(false);
        topProductsChart.setAnimated(false);
        loadCharts();
    }

    private void loadCharts() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<Map<String, Object>> ventas = statisticsService.getVentasPorDia();
                List<Map<String, Object>> top    = statisticsService.getTopProductos();
                Platform.runLater(() -> {
                    fillSalesChart(ventas);
                    fillTopChart(top);
                });
                return null;
            }
        };
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Statistics Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    private void fillSalesChart(List<Map<String, Object>> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        for (Map<String, Object> entry : data) {
            String fecha = String.valueOf(entry.getOrDefault("fecha", ""));
            double total = ((Number) entry.getOrDefault("total", 0)).doubleValue();
            series.getData().add(new XYChart.Data<>(fecha, total));
        }
        salesChart.getData().setAll(series);
    }

    private void fillTopChart(List<Map<String, Object>> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Units Sold");
        for (Map<String, Object> entry : data) {
            String nombre   = String.valueOf(entry.getOrDefault("nombre", ""));
            double cantidad = ((Number) entry.getOrDefault("cantidad", 0)).doubleValue();
            series.getData().add(new XYChart.Data<>(nombre, cantidad));
        }
        topProductsChart.getData().setAll(series);
    }
}

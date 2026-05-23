package com.pos.controllers;

import com.pos.models.*;
import com.pos.services.ClientService;
import com.pos.services.InvoiceService;
import com.pos.services.ProductService;
import com.pos.utils.AlertUtil;
import com.pos.utils.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class InvoiceDialogController {

    @FXML private ComboBox<Client> clientCombo;
    @FXML private VBox linesContainer;
    @FXML private Label totalLabel;
    @FXML private Button saveBtn;

    private final ClientService clientService = new ClientService();
    private final ProductService productService = new ProductService();
    private final InvoiceService invoiceService = new InvoiceService();

    private List<Client> clients = List.of();
    private List<Product> products = List.of();
    private final List<LineRow> rows = new ArrayList<>();
    private boolean saved = false;

    @FXML
    public void initialize() {
        clientCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Client c) { return c != null ? c.getNombre() : ""; }
            @Override public Client fromString(String s) { return null; }
        });
        loadData();
    }

    private void loadData() {
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                clients = clientService.getAll();
                products = productService.getAll();
                Platform.runLater(() -> {
                    clientCombo.setItems(FXCollections.observableArrayList(clients));
                    if (!clients.isEmpty()) clientCombo.setValue(clients.get(0));
                    addLine();
                });
                return null;
            }
        };
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Load Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    @FXML
    private void addLine() {
        LineRow row = new LineRow(products, this::recalcTotal);
        rows.add(row);
        HBox rowBox = row.getView();
        rowBox.setUserData(row);

        Button removeBtn = new Button("X");
        removeBtn.getStyleClass().add("btn-danger-small");
        removeBtn.setOnAction(e -> {
            rows.remove(row);
            linesContainer.getChildren().remove(rowBox);
            recalcTotal();
        });
        rowBox.getChildren().add(removeBtn);
        linesContainer.getChildren().add(rowBox);
    }

    private void recalcTotal() {
        double total = rows.stream()
                .mapToDouble(r -> r.getSubtotal())
                .sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void handleSave() {
        if (clientCombo.getValue() == null) {
            AlertUtil.showError("Validation", "Please select a client.");
            return;
        }
        if (rows.isEmpty()) {
            AlertUtil.showError("Validation", "Add at least one product line.");
            return;
        }
        for (LineRow row : rows) {
            if (row.getProduct() == null || row.getQuantity() <= 0) {
                AlertUtil.showError("Validation", "All lines must have a product and valid quantity.");
                return;
            }
        }

        Invoice invoice = new Invoice();
        invoice.setCliente(clientCombo.getValue());

        Cashier cashier = new Cashier();
        cashier.setUsuario(SessionManager.getInstance().getUsername());
        invoice.setCajero(cashier);

        List<InvoiceLine> lineas = new ArrayList<>();
        for (LineRow row : rows) {
            InvoiceLine line = new InvoiceLine();
            line.setProducto(row.getProduct());
            line.setCantidad(row.getQuantity());
            line.setPrecioUnitario(row.getProduct().getPrecio());
            line.setSubtotal(row.getSubtotal());
            lineas.add(line);
        }
        invoice.setLineas(lineas);

        saveBtn.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                invoiceService.create(invoice);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            saved = true;
            ((Stage) saveBtn.getScene().getWindow()).close();
        });
        task.setOnFailed(e -> {
            saveBtn.setDisable(false);
            Platform.runLater(() ->
                    AlertUtil.showError("Save Error", task.getException().getMessage()));
        });
        new Thread(task).start();
    }

    @FXML
    private void handleCancel() {
        ((Stage) saveBtn.getScene().getWindow()).close();
    }

    public boolean isSaved() { return saved; }

    // Inner helper for a product line row
    static class LineRow {
        private final ComboBox<Product> productCombo = new ComboBox<>();
        private final TextField qtyField = new TextField("1");
        private final Label subtotalLabel = new Label("$0.00");
        private final Runnable onChanged;

        LineRow(List<Product> products, Runnable onChanged) {
            this.onChanged = onChanged;
            productCombo.setItems(FXCollections.observableArrayList(products));
            productCombo.setConverter(new StringConverter<>() {
                @Override public String toString(Product p) { return p != null ? p.getNombre() : ""; }
                @Override public Product fromString(String s) { return null; }
            });
            productCombo.setPrefWidth(220);
            productCombo.setOnAction(e -> refresh());

            qtyField.setPrefWidth(60);
            qtyField.textProperty().addListener((obs, o, n) -> refresh());

            subtotalLabel.setPrefWidth(90);
            subtotalLabel.setStyle("-fx-text-fill: #a6e3a1;");
        }

        private void refresh() {
            subtotalLabel.setText(String.format("$%.2f", getSubtotal()));
            onChanged.run();
        }

        HBox getView() {
            HBox box = new HBox(10, productCombo, new Label("Qty:"), qtyField, subtotalLabel);
            box.setAlignment(Pos.CENTER_LEFT);
            box.setPadding(new Insets(4, 0, 4, 0));
            return box;
        }

        Product getProduct() { return productCombo.getValue(); }

        int getQuantity() {
            try { return Math.max(0, Integer.parseInt(qtyField.getText().trim())); }
            catch (NumberFormatException e) { return 0; }
        }

        double getSubtotal() {
            Product p = getProduct();
            return p != null ? p.getPrecio() * getQuantity() : 0;
        }
    }
}

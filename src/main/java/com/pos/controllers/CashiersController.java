package com.pos.controllers;

import com.pos.models.Cashier;
import com.pos.services.CashierService;
import com.pos.utils.AlertUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

public class CashiersController {

    @FXML private TableView<Cashier> tableView;
    @FXML private TableColumn<Cashier, String> idCol;
    @FXML private TableColumn<Cashier, String> nombreCol;
    @FXML private TableColumn<Cashier, String> usuarioCol;
    @FXML private TableColumn<Cashier, String> rolCol;

    private final CashierService cashierService = new CashierService();
    private final ObservableList<Cashier> cashiers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        nombreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        usuarioCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario()));
        rolCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRol()));
        tableView.setPlaceholder(new Label("No cashiers found."));
        loadData();
    }

    private void loadData() {
        Task<List<Cashier>> task = new Task<>() {
            @Override protected List<Cashier> call() throws Exception { return cashierService.getAll(); }
        };
        task.setOnSucceeded(e -> cashiers.setAll(task.getValue()));
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Load Error", task.getException().getMessage())));
        tableView.setItems(cashiers);
        new Thread(task).start();
    }

    @FXML private void handleAdd() { showDialog(null); }

    @FXML
    private void handleEdit() {
        Cashier sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select a cashier to edit."); return; }
        showDialog(sel);
    }

    @FXML
    private void handleDelete() {
        Cashier sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select a cashier to delete."); return; }
        if (!AlertUtil.showConfirm("Delete Cashier", "Delete \"" + sel.getNombre() + "\"?")) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception { cashierService.delete(sel.getId()); return null; }
        };
        task.setOnSucceeded(e -> loadData());
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Delete Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    private void showDialog(Cashier existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Cashier" : "Edit Cashier");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(24));

        TextField nameField  = new TextField(existing != null ? existing.getNombre() : "");
        TextField userField  = new TextField(existing != null ? existing.getUsuario() : "");
        PasswordField passField = new PasswordField();
        passField.setPromptText(existing != null ? "(leave blank to keep)" : "Password");
        ComboBox<String> rolCombo = new ComboBox<>();
        rolCombo.getItems().addAll("CAJERO", "ADMIN");
        rolCombo.setValue(existing != null && existing.getRol() != null ? existing.getRol() : "CAJERO");

        grid.add(new Label("Name:"),     0, 0); grid.add(nameField,  1, 0);
        grid.add(new Label("Username:"), 0, 1); grid.add(userField,  1, 1);
        grid.add(new Label("Password:"), 0, 2); grid.add(passField,  1, 2);
        grid.add(new Label("Role:"),     0, 3); grid.add(rolCombo,   1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        Cashier c = existing != null ? existing : new Cashier();
        c.setNombre(nameField.getText().trim());
        c.setUsuario(userField.getText().trim());
        if (!passField.getText().isEmpty()) c.setContrasena(passField.getText());
        c.setRol(rolCombo.getValue());

        Task<Void> save = new Task<>() {
            @Override protected Void call() throws Exception {
                if (existing == null) cashierService.create(c);
                else cashierService.update(existing.getId(), c);
                return null;
            }
        };
        save.setOnSucceeded(e -> loadData());
        save.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Save Error", save.getException().getMessage())));
        new Thread(save).start();
    }
}

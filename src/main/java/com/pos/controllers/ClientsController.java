package com.pos.controllers;

import com.pos.models.Client;
import com.pos.services.ClientService;
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

public class ClientsController {

    @FXML private TableView<Client> tableView;
    @FXML private TableColumn<Client, String> idCol;
    @FXML private TableColumn<Client, String> nombreCol;
    @FXML private TableColumn<Client, String> emailCol;
    @FXML private TableColumn<Client, String> telefonoCol;

    private final ClientService clientService = new ClientService();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        nombreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        telefonoCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        tableView.setPlaceholder(new Label("No clients found."));
        loadData();
    }

    private void loadData() {
        Task<List<Client>> task = new Task<>() {
            @Override protected List<Client> call() throws Exception { return clientService.getAll(); }
        };
        task.setOnSucceeded(e -> clients.setAll(task.getValue()));
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Load Error", task.getException().getMessage())));
        tableView.setItems(clients);
        new Thread(task).start();
    }

    @FXML private void handleAdd()  { showDialog(null); }

    @FXML
    private void handleEdit() {
        Client sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select a client to edit."); return; }
        showDialog(sel);
    }

    @FXML
    private void handleDelete() {
        Client sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select a client to delete."); return; }
        if (!AlertUtil.showConfirm("Delete Client", "Delete \"" + sel.getNombre() + "\"?")) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception { clientService.delete(sel.getId()); return null; }
        };
        task.setOnSucceeded(e -> loadData());
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Delete Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    private void showDialog(Client existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Client" : "Edit Client");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(24));

        TextField nameField  = new TextField(existing != null ? existing.getNombre() : "");
        TextField emailField = new TextField(existing != null ? existing.getEmail() : "");
        TextField phoneField = new TextField(existing != null ? existing.getTelefono() : "");

        grid.add(new Label("Name:"),  0, 0); grid.add(nameField,  1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2); grid.add(phoneField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        Client c = existing != null ? existing : new Client();
        c.setNombre(nameField.getText().trim());
        c.setEmail(emailField.getText().trim());
        c.setTelefono(phoneField.getText().trim());

        Task<Void> save = new Task<>() {
            @Override protected Void call() throws Exception {
                if (existing == null) clientService.create(c);
                else clientService.update(existing.getId(), c);
                return null;
            }
        };
        save.setOnSucceeded(e -> loadData());
        save.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Save Error", save.getException().getMessage())));
        new Thread(save).start();
    }
}

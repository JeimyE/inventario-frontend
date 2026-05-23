package com.pos.controllers;

import com.pos.models.Category;
import com.pos.services.CategoryService;
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

public class CategoriesController {

    @FXML private TableView<Category> tableView;
    @FXML private TableColumn<Category, String> idCol;
    @FXML private TableColumn<Category, String> nombreCol;

    private final CategoryService categoryService = new CategoryService();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        nombreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        tableView.setPlaceholder(new Label("No categories found."));
        tableView.setItems(categories);
        loadData();
    }

    private void loadData() {
        Task<List<Category>> task = new Task<>() {
            @Override protected List<Category> call() throws Exception { return categoryService.getAll(); }
        };
        task.setOnSucceeded(e -> categories.setAll(task.getValue()));
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Load Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    @FXML
    private void handleAdd() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Category");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(24));

        TextField nameField = new TextField();
        nameField.setPromptText("Category name");
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;
        if (nameField.getText().isBlank()) { AlertUtil.showError("Validation", "Name cannot be empty."); return; }

        Category cat = new Category();
        cat.setNombre(nameField.getText().trim());

        Task<Void> save = new Task<>() {
            @Override protected Void call() throws Exception { categoryService.create(cat); return null; }
        };
        save.setOnSucceeded(e -> loadData());
        save.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Save Error", save.getException().getMessage())));
        new Thread(save).start();
    }

    @FXML
    private void handleDelete() {
        Category sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select a category to delete."); return; }
        if (!AlertUtil.showConfirm("Delete Category", "Delete \"" + sel.getNombre() + "\"?")) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception { categoryService.delete(sel.getId()); return null; }
        };
        task.setOnSucceeded(e -> loadData());
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Delete Error", task.getException().getMessage())));
        new Thread(task).start();
    }
}

package com.pos.controllers;

import com.pos.models.Category;
import com.pos.models.Product;
import com.pos.services.CategoryService;
import com.pos.services.ProductService;
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
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

public class ProductsController {

    @FXML private TableView<Product> tableView;
    @FXML private TableColumn<Product, String> idCol;
    @FXML private TableColumn<Product, String> nombreCol;
    @FXML private TableColumn<Product, String> precioCol;
    @FXML private TableColumn<Product, String> stockCol;
    @FXML private TableColumn<Product, String> categoriaCol;
    @FXML private ComboBox<String> categoryFilter;

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private List<Category> categories = List.of();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        nombreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        precioCol.setCellValueFactory(c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().getPrecio())));
        stockCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        categoriaCol.setCellValueFactory(c -> {
            Category cat = c.getValue().getCategoria();
            return new SimpleStringProperty(cat != null ? cat.getNombre() : "");
        });
        tableView.setPlaceholder(new Label("No products found."));
        loadData();
    }

    private void loadData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<Product> products = productService.getAll();
                categories = categoryService.getAll();
                Platform.runLater(() -> {
                    allProducts.setAll(products);
                    tableView.setItems(allProducts);
                    populateCategoryFilter();
                });
                return null;
            }
        };
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Load Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    private void populateCategoryFilter() {
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All Categories");
        categories.forEach(c -> categoryFilter.getItems().add(c.getNombre()));
        categoryFilter.setValue("All Categories");
    }

    @FXML
    private void filterByCategory() {
        String selected = categoryFilter.getValue();
        if (selected == null || selected.equals("All Categories")) {
            tableView.setItems(allProducts);
        } else {
            tableView.setItems(allProducts.filtered(
                    p -> p.getCategoria() != null && selected.equals(p.getCategoria().getNombre())));
        }
    }

    @FXML
    private void handleAdd() { showDialog(null); }

    @FXML
    private void handleEdit() {
        Product sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select a product to edit."); return; }
        showDialog(sel);
    }

    @FXML
    private void handleDelete() {
        Product sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select a product to delete."); return; }
        if (!AlertUtil.showConfirm("Delete Product", "Delete \"" + sel.getNombre() + "\"?")) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                productService.delete(sel.getId());
                return null;
            }
        };
        task.setOnSucceeded(e -> loadData());
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Delete Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    private void showDialog(Product existing) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Product" : "Edit Product");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(24));

        TextField nameField  = new TextField(existing != null ? existing.getNombre() : "");
        TextField priceField = new TextField(existing != null ? String.valueOf(existing.getPrecio()) : "");
        TextField stockField = new TextField(existing != null ? String.valueOf(existing.getStock()) : "");

        ComboBox<Category> catCombo = new ComboBox<>();
        catCombo.getItems().addAll(categories);
        catCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Category c) { return c != null ? c.getNombre() : ""; }
            @Override public Category fromString(String s) { return null; }
        });
        if (existing != null && existing.getCategoria() != null) {
            categories.stream()
                    .filter(c -> c.getId().equals(existing.getCategoria().getId()))
                    .findFirst().ifPresent(catCombo::setValue);
        }
        catCombo.setPrefWidth(200);

        grid.add(new Label("Name:"),     0, 0); grid.add(nameField,  1, 0);
        grid.add(new Label("Price:"),    0, 1); grid.add(priceField, 1, 1);
        grid.add(new Label("Stock:"),    0, 2); grid.add(stockField, 1, 2);
        grid.add(new Label("Category:"), 0, 3); grid.add(catCombo,   1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        try {
            Product p = existing != null ? existing : new Product();
            p.setNombre(nameField.getText().trim());
            p.setPrecio(Double.parseDouble(priceField.getText().trim()));
            p.setStock(Integer.parseInt(stockField.getText().trim()));
            p.setCategoria(catCombo.getValue());

            Task<Void> save = new Task<>() {
                @Override protected Void call() throws Exception {
                    if (existing == null) productService.create(p);
                    else productService.update(existing.getId(), p);
                    return null;
                }
            };
            save.setOnSucceeded(e -> loadData());
            save.setOnFailed(e -> Platform.runLater(() ->
                    AlertUtil.showError("Save Error", save.getException().getMessage())));
            new Thread(save).start();
        } catch (NumberFormatException ex) {
            AlertUtil.showError("Invalid Input", "Price must be a decimal and stock an integer.");
        }
    }
}

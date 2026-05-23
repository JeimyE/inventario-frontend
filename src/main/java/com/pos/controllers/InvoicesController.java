package com.pos.controllers;

import com.pos.MainApp;
import com.pos.models.Invoice;
import com.pos.services.InvoiceService;
import com.pos.utils.AlertUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class InvoicesController {

    @FXML private TableView<Invoice> tableView;
    @FXML private TableColumn<Invoice, String> idCol;
    @FXML private TableColumn<Invoice, String> fechaCol;
    @FXML private TableColumn<Invoice, String> clienteCol;
    @FXML private TableColumn<Invoice, String> cajeroCol;
    @FXML private TableColumn<Invoice, String> totalCol;

    private final InvoiceService invoiceService = new InvoiceService();
    private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        fechaCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha()));
        clienteCol.setCellValueFactory(c -> {
            var cl = c.getValue().getCliente();
            return new SimpleStringProperty(cl != null ? cl.getNombre() : "");
        });
        cajeroCol.setCellValueFactory(c -> {
            var ca = c.getValue().getCajero();
            return new SimpleStringProperty(ca != null ? ca.getNombre() : "");
        });
        totalCol.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("$%.2f", c.getValue().getTotal())));

        tableView.setPlaceholder(new Label("No invoices found."));
        tableView.setItems(invoices);
        loadData();
    }

    private void loadData() {
        Task<List<Invoice>> task = new Task<>() {
            @Override protected List<Invoice> call() throws Exception { return invoiceService.getAll(); }
        };
        task.setOnSucceeded(e -> invoices.setAll(task.getValue()));
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Load Error", task.getException().getMessage())));
        new Thread(task).start();
    }

    @FXML
    private void handleNewInvoice() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/pos/fxml/invoice-dialog.fxml"));
            Parent root = loader.load();
            InvoiceDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("New Invoice");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainApp.getPrimaryStage());
            Scene scene = new Scene(root, 750, 560);
            scene.getStylesheets().add(
                    MainApp.class.getResource("/com/pos/css/dark-theme.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaved()) loadData();
        } catch (Exception ex) {
            AlertUtil.showError("Error", "Could not open invoice dialog: " + ex.getMessage());
        }
    }

    @FXML
    private void handleDownloadPdf() {
        Invoice sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.showInfo("No Selection", "Select an invoice to download."); return; }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Invoice PDF");
        chooser.setInitialFileName("invoice-" + sel.getId() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showSaveDialog(MainApp.getPrimaryStage());
        if (file == null) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                byte[] bytes = invoiceService.downloadPdf(sel.getId());
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> AlertUtil.showInfo("Download Complete", "Saved to: " + file.getAbsolutePath()));
        task.setOnFailed(e -> Platform.runLater(() ->
                AlertUtil.showError("Download Error", task.getException().getMessage())));
        new Thread(task).start();
    }
}

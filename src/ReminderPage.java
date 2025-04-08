import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderPage extends Application {
    private ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();
    private TableView<InventoryItem> tableView;
    private ComboBox<String> categoryFilter;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reminders - Expiring Soon");

        Label header = new Label("Items Expiring Within 2 Weeks");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Table Setup
        tableView = new TableView<>();
        TableColumn<InventoryItem, String> nameColumn = new TableColumn<>("Item Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<InventoryItem, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<InventoryItem, String> expiryDateColumn = new TableColumn<>("Expiry Date");
        expiryDateColumn.setCellValueFactory(cellData -> cellData.getValue().expiryDateProperty());

        tableView.getColumns().addAll(nameColumn, categoryColumn, expiryDateColumn);

        // Category Filter
        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Filter by Category");
        categoryFilter.setOnAction(e -> filterItemsByCategory());

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.close());

        HBox filterBox = new HBox(10, categoryFilter, backButton);
        filterBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, header, tableView, filterBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        loadInventoryItems(); // Load initial inventory items
    }

    private void loadInventoryItems() {
        // Sample data (Replace with actual inventory fetching)
        inventoryItems.add(new InventoryItem("Milk", "Dairy", "2025-03-30"));
        inventoryItems.add(new InventoryItem("Apples", "Fruit", "2025-03-25"));
        inventoryItems.add(new InventoryItem("Chicken", "Meat", "2025-03-22"));
        inventoryItems.add(new InventoryItem("Bread", "Bakery", "2025-03-28"));
        inventoryItems.add(new InventoryItem("Cheese", "Dairy", "2025-03-21"));

        updateTable();
    }

    private void updateTable() {
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksLater = today.plusWeeks(2);

        // Filter only items expiring within two weeks
        List<InventoryItem> filteredItems = inventoryItems.stream()
                .filter(item -> {
                    LocalDate expiryDate = LocalDate.parse(item.getExpiryDate(), DateTimeFormatter.ISO_DATE);
                    return !expiryDate.isBefore(today) && !expiryDate.isAfter(twoWeeksLater);
                })
                .collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(filteredItems));

        // Update category dropdown
        categoryFilter.setItems(FXCollections.observableArrayList(
                filteredItems.stream().map(InventoryItem::getCategory).distinct().collect(Collectors.toList())
        ));
    }

    private void filterItemsByCategory() {
        String selectedCategory = categoryFilter.getValue();
        if (selectedCategory != null) {
            List<InventoryItem> filteredList = inventoryItems.stream()
                    .filter(item -> item.getCategory().equals(selectedCategory))
                    .collect(Collectors.toList());
            tableView.setItems(FXCollections.observableArrayList(filteredList));
        } else {
            updateTable();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


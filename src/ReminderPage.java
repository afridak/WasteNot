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

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderPage extends Application {
    private ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();
    private TableView<InventoryItem> tableView;
    private ComboBox<String> categoryFilter;
    private Connection connection;

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

        connectDatabase();
        loadInventoryItems(); // Load initial inventory items
    }

    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:inventory.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInventoryItems() {
        inventoryItems.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name, category, expiry_date FROM inventory");
            LocalDate today = LocalDate.now();
            LocalDate twoWeeksLater = today.plusWeeks(2);

            while (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                String expiryDate = rs.getString("expiry_date");
                LocalDate expiry = LocalDate.parse(expiryDate, DateTimeFormatter.ISO_DATE);

                if (!expiry.isBefore(today) && !expiry.isAfter(twoWeeksLater)) {
                    inventoryItems.add(new InventoryItem(name, category, expiryDate));
                }
            }
            updateTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTable() {
        tableView.setItems(FXCollections.observableArrayList(inventoryItems));
        categoryFilter.setItems(FXCollections.observableArrayList(
                inventoryItems.stream().map(InventoryItem::getCategory).distinct().collect(Collectors.toList())
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



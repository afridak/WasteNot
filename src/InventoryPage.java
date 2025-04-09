import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InventoryPage extends Application {
    private Connection connection;
    private ListView<String> inventoryListView;
    private TextField itemNameField;
    private ComboBox<String> categoryBox;
    private DatePicker expiryDatePicker;

    @Override
    public void start(Stage primaryStage) {
        connectDatabase();

        Label titleLabel = new Label("Manage Inventory");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Inventory List
        inventoryListView = new ListView<>();
        loadInventory();

        // Item Details Input
        itemNameField = new TextField();
        itemNameField.setPromptText("Item Name");

        categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Dairy", "Vegetables", "Meat", "Grains", "Fruits", "Others");
        categoryBox.setPromptText("Select Category");

        expiryDatePicker = new DatePicker();
        expiryDatePicker.setPromptText("Expiration Date");

        // Buttons
        Button addButton = new Button("Add Item");
        Button updateButton = new Button("Update Item");
        Button deleteButton = new Button("Delete Item");

        addButton.setOnAction(e -> addItem());
        updateButton.setOnAction(e -> updateItem());
        deleteButton.setOnAction(e -> deleteItem());

        // Layout
        VBox inputBox = new VBox(10, itemNameField, categoryBox, expiryDatePicker, addButton, updateButton, deleteButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));

        HBox mainLayout = new HBox(20, inventoryListView, inputBox);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User - Manage Inventory");
        primaryStage.show();
    }

    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:inventory.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS inventory (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category TEXT, expiry_date TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInventory() {
        inventoryListView.getItems().clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name, category, expiry_date FROM inventory");
            while (rs.next()) {
                String item = rs.getString("name") + " (" + rs.getString("category") + ") - Exp: " + rs.getString("expiry_date");
                inventoryListView.getItems().add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addItem() {
        String name = itemNameField.getText();
        String category = categoryBox.getValue();
        LocalDate expiryDate = expiryDatePicker.getValue();

        if (name.isEmpty() || category == null || expiryDate == null) {
            showAlert("Please fill in all fields.");
            return;
        }

        String expiryDateString = expiryDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO inventory (name, category, expiry_date) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, expiryDateString);
            ps.executeUpdate();
            loadInventory();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateItem() {
        String selectedItem = inventoryListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Please select an item to update.");
            return;
        }

        String newName = itemNameField.getText();
        String newCategory = categoryBox.getValue();
        LocalDate newExpiryDate = expiryDatePicker.getValue();

        if (newName.isEmpty() || newCategory == null || newExpiryDate == null) {
            showAlert("Please fill in all fields.");
            return;
        }

        String expiryDateString = newExpiryDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String oldName = selectedItem.split(" \\(")[0];

        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE inventory SET name = ?, category = ?, expiry_date = ? WHERE name = ?");
            ps.setString(1, newName);
            ps.setString(2, newCategory);
            ps.setString(3, expiryDateString);
            ps.setString(4, oldName);
            ps.executeUpdate();
            loadInventory();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem() {
        String selectedItem = inventoryListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Please select an item to delete.");
            return;
        }

        String itemName = selectedItem.split(" \\(")[0];

        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM inventory WHERE name = ?");
            ps.setString(1, itemName);
            ps.executeUpdate();
            loadInventory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        itemNameField.clear();
        categoryBox.getSelectionModel().clearSelection();
        expiryDatePicker.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

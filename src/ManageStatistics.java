import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageStatistics extends Application {

    private TableView<Statistic> statsTable;
    private ObservableList<Statistic> statsData;
    private TextField titleField, descriptionField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Manage Statistics & Tips - WasteNot");

        // Table setup
        statsTable = new TableView<>();
        statsData = FXCollections.observableArrayList();
        fetchStatisticsFromDatabase();

        // Columns
        TableColumn<Statistic, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Statistic, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        statsTable.getColumns().addAll(titleCol, descriptionCol);
        statsTable.setItems(statsData);

        // Form for adding/updating statistics
        titleField = new TextField();
        titleField.setPromptText("Title");

        descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        Button addButton = new Button("Add/Update");
        addButton.setOnAction(e -> addOrUpdateStatistic());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteSelectedStatistic());

        // Layout
        HBox formLayout = new HBox(10, titleField, descriptionField, addButton, deleteButton);
        formLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, statsTable, formLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1F8E9;");

        // Scene setup
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchStatisticsFromDatabase() {
        String url = "jdbc:sqlite:statistics.db";
        String createTableSQL = "CREATE TABLE IF NOT EXISTS statistics ("
                + "title TEXT PRIMARY KEY,"
                + "description TEXT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
            pstmt.execute(); // Create the table if it doesn't exist
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fetch data
        String query = "SELECT title, description FROM statistics";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                statsData.add(new Statistic(title, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOrUpdateStatistic() {
        String title = titleField.getText();
        String description = descriptionField.getText();

        if (!title.isEmpty() && !description.isEmpty()) {
            String url = "jdbc:sqlite:statistics.db";
            String query = "INSERT OR REPLACE INTO statistics (title, description) VALUES (?, ?)";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, title);
                pstmt.setString(2, description);
                pstmt.executeUpdate();

                // Refresh table
                statsData.clear();
                fetchStatisticsFromDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText("Empty Fields");
            alert.setContentText("Please fill in both title and description.");
            alert.showAndWait();
        }
    }

    private void deleteSelectedStatistic() {
        Statistic selectedStat = statsTable.getSelectionModel().getSelectedItem();
        if (selectedStat != null) {
            String url = "jdbc:sqlite:statistics.db";
            String query = "DELETE FROM statistics WHERE title = ?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, selectedStat.getTitle());
                pstmt.executeUpdate();
                statsData.remove(selectedStat); // Remove from the table view
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Statistic Selected");
            alert.setContentText("Please select a statistic to delete.");
            alert.showAndWait();
        }
    }

    public static class Statistic {
        private final String title;
        private final String description;

        public Statistic(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
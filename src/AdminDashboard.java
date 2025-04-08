import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AdminDashboard extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Dashboard - WasteNot");

        // Title
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // Manage Recipes Button
        Button manageRecipesButton = new Button("Manage Recipes");
        manageRecipesButton.setOnAction(e -> openManageRecipes());

        // Manage Users Button
        Button manageUsersButton = new Button("Manage Users");
        manageUsersButton.setOnAction(e -> openManageUsers());

        // Manage Statistics & Tips Button
        Button manageStatisticsButton = new Button("Manage Statistics & Tips");
        manageStatisticsButton.setOnAction(e -> openManageStatistics());

        manageRecipesButton.setStyle("-fx-background-color: #E91E64; -fx-text-fill: white; -fx-font-size: 14px;");
        manageUsersButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px;");
        manageStatisticsButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px;");

        // Layout
        VBox layout = new VBox(15, titleLabel, manageRecipesButton, manageUsersButton, manageStatisticsButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1F8E9;");

        // Scene Setup
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createDatabase() {
        String url = "jdbc:sqlite:statistics.db";
        String sql = "CREATE TABLE IF NOT EXISTS statistics ("
                + "title TEXT PRIMARY KEY,"
                + "description TEXT NOT NULL"
                + ");";

        System.out.println("Attempting to create database and table...");
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            System.out.println("Database and table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
        }
    }

    private void openManageRecipes() {
        ManageRecipes manageRecipesPage = new ManageRecipes();
        Stage manageRecipesStage = new Stage();
        manageRecipesPage.start(manageRecipesStage); // Open the ManageRecipes window
    }

    private void openManageUsers() {
        ManageUsers manageUsersPage = new ManageUsers();
        Stage manageUsersStage = new Stage();
        manageUsersPage.start(manageUsersStage); // Open the ManageUsers window
    }

    private void openManageStatistics() {
        ManageStatistics manageStatisticsPage = new ManageStatistics();
        Stage manageStatisticsStage = new Stage();
        manageStatisticsPage.start(manageStatisticsStage); // Open the ManageStatistics window
    }

    public static void main(String[] args) {
        launch(args);
    }
}

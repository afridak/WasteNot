import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Statistics & Tips - WasteNot");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1F8E9;");

        // Fetch and display statistics
        String url = "jdbc:sqlite:statistics.db";
        String query = "SELECT title, description FROM statistics";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");

                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

                Label descriptionLabel = new Label(description);
                descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50;");

                VBox entryBox = new VBox(5, titleLabel, descriptionLabel);
                entryBox.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10px; -fx-border-radius: 5px;");

                layout.getChildren().add(entryBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Scene setup
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

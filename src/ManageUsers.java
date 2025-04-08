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

public class ManageUsers extends Application {

    private TableView<User> userTable;
    private ObservableList<User> userData;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Manage Users - WasteNot");

        // Table setup
        userTable = new TableView<>();
        userData = FXCollections.observableArrayList();
        fetchUsersFromDatabase();

        // Columns
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        userTable.getColumns().addAll(usernameCol, emailCol);
        userTable.setItems(userData);

        // Delete button
        Button deleteButton = new Button("Delete User");
        deleteButton.setOnAction(e -> deleteSelectedUser());

        // Layout
        VBox layout = new VBox(10, userTable, deleteButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1F8E9;");

        // Scene setup
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchUsersFromDatabase() {
        String url = "jdbc:sqlite:users.db";
        String query = "SELECT username, email FROM users";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                userData.add(new User(username, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String url = "jdbc:sqlite:users.db";
            String query = "DELETE FROM users WHERE username = ?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, selectedUser.getUsername());
                pstmt.executeUpdate();
                userData.remove(selectedUser); // Remove from the table view
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a user to delete.");
            alert.showAndWait();
        }
    }

    public static class User {
        private final String username;
        private final String email;

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

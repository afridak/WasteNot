import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {
    private Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:users.db");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("WasteNot");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // Username Field
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        // Email Field
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");

        // Password Field
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        // Role Selection
        Label roleLabel = new Label("Select Role:");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("User", "Admin");
        roleBox.setValue("User"); // Default role

        // Buttons
        Button signupButton = new Button("Sign Up");
        signupButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-font-size: 14px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-font-size: 14px;");

        // Database Connection
        createDatabase();

        // Sign Up Logic
        signupButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String role = roleBox.getValue();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "All fields must be filled.");
                return;
            }

            try (Connection conn = connectDB();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)")) {

                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, role);
                stmt.executeUpdate();

                showAlert("Success", "Account created successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to create account.");
            }
        });

        // Login Logic
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Username and Password required!");
                return;
            }

            try (Connection conn = connectDB();
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ? AND password = ?")) {

                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("Admin".equals(role)) {
                        new AdminDashboard().start(new Stage());
                    } else {
                        new UserDashboard().start(new Stage());
                    }
                    primaryStage.close();
                } else {
                    showAlert("Error", "Invalid login credentials.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "Login failed.");
            }
        });

        // Layout
        VBox vbox = new VBox(10, titleLabel, userLabel, usernameField, emailLabel, emailField, passLabel, passwordField, roleLabel, roleBox, signupButton, loginButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #F1F8E9; -fx-padding: 30px; -fx-border-radius: 10px;");

        primaryStage.setScene(new Scene(vbox, 400, 450));
        primaryStage.setTitle("Login / Sign Up - WasteNot");
        primaryStage.show();
    }

    private void createDatabase() {
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT, email TEXT, password TEXT, role TEXT)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



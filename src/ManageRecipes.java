import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class ManageRecipes extends Application {
    private Connection connection;
    private ListView<String> recipeListView;
    private TextField nameField;
    private TextArea ingredientsField, instructionsField;

    @Override
    public void start(Stage primaryStage) {
        connectDatabase();

        Label titleLabel = new Label("Manage Recipes");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Recipe List
        recipeListView = new ListView<>();
        loadRecipes();

        // Recipe Details Input
        nameField = new TextField();
        nameField.setPromptText("Recipe Name");

        ingredientsField = new TextArea();
        ingredientsField.setPromptText("Ingredients");
        ingredientsField.setPrefRowCount(3);

        instructionsField = new TextArea();
        instructionsField.setPromptText("Instructions");
        instructionsField.setPrefRowCount(3);

        // Buttons
        Button addButton = new Button("Add Recipe");
        Button updateButton = new Button("Update Recipe");
        Button deleteButton = new Button("Delete Recipe");

        addButton.setOnAction(e -> addRecipe());
        updateButton.setOnAction(e -> updateRecipe());
        deleteButton.setOnAction(e -> deleteRecipe());

        // Layout
        VBox inputBox = new VBox(10, nameField, ingredientsField, instructionsField, addButton, updateButton, deleteButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));

        HBox mainLayout = new HBox(20, recipeListView, inputBox);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin - Manage Recipes");
        primaryStage.show();

        //Listener to load recipe details when selected
        recipeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadRecipeDetails(newValue);
            }
        });
    }

    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:recipes.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ingredients TEXT, instructions TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecipes() {
        recipeListView.getItems().clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM recipes");
            while (rs.next()) {
                recipeListView.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecipeDetails(String recipeName) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM recipes WHERE name = ?");
            ps.setString(1, recipeName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Populate fields with the recipe details
                nameField.setText(rs.getString("name"));
                ingredientsField.setText(rs.getString("ingredients"));
                instructionsField.setText(rs.getString("instructions"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRecipe() {
        String name = nameField.getText();
        String ingredients = ingredientsField.getText();
        String instructions = instructionsField.getText();

        if (name.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO recipes (name, ingredients, instructions) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, ingredients);
            ps.setString(3, instructions);
            ps.executeUpdate();
            loadRecipes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecipe() {
        String selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            showAlert("Please select a recipe to update.");
            return;
        }

        String newName = nameField.getText();
        String newIngredients = ingredientsField.getText();
        String newInstructions = instructionsField.getText();

        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE recipes SET name = ?, ingredients = ?, instructions = ? WHERE name = ?");
            ps.setString(1, newName);
            ps.setString(2, newIngredients);
            ps.setString(3, newInstructions);
            ps.setString(4, selectedRecipe);
            ps.executeUpdate();
            loadRecipes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecipe() {
        String selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            showAlert("Please select a recipe to delete.");
            return;
        }

        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM recipes WHERE name = ?");
            ps.setString(1, selectedRecipe);
            ps.executeUpdate();
            loadRecipes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        ingredientsField.clear();
        instructionsField.clear();
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



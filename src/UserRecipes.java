import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class UserRecipes extends Application {
    private StackPane stackPane;
    private ListView<String> recipeListView;
    private TextArea recipeDetailsTextArea;
    private Button backButton;
    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        // StackPane to layer the views
        stackPane = new StackPane();

        // Create the recipe list view and details view
        recipeListView = new ListView<>();
        recipeDetailsTextArea = new TextArea();
        backButton = new Button("Back to Recipes List");

        // Initialize the database and load the recipes
        connectDatabase();
        loadRecipes();

        // When a recipe is clicked, show the recipe details
        recipeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showRecipeDetails(newValue));

        // Set the Back button action
        backButton.setOnAction(e -> goBackToRecipeList());

        // Layout for Recipe List
        VBox listLayout = new VBox(10, new Label("Recipes:"), recipeListView);
        listLayout.setAlignment(Pos.CENTER);
        listLayout.setStyle("-fx-background-color: #F1F8E9; -fx-padding: 20px; -fx-border-radius: 10px;");

        // Layout for Recipe Details
        VBox detailsLayout = new VBox(10, recipeDetailsTextArea, backButton);
        detailsLayout.setAlignment(Pos.CENTER);
        detailsLayout.setStyle("-fx-background-color: #FFF3E0; -fx-padding: 20px; -fx-border-radius: 10px;");

        // Initially, show the recipe list
        stackPane.getChildren().add(listLayout);

        // Scene
        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setTitle("User Recipes - WasteNot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:recipes.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecipes() {
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

    private void showRecipeDetails(String recipeName) {
        if (recipeName != null) {
            // Fetch the recipe details from the database
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT ingredients, instructions FROM recipes WHERE name = ?");
                ps.setString(1, recipeName);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String ingredients = rs.getString("ingredients");
                    String instructions = rs.getString("instructions");
                    String details = "Ingredients:\n" + ingredients + "\n\nInstructions:\n" + instructions;
                    recipeDetailsTextArea.setText(details);
                }

                // Swap the views: Show recipe details
                stackPane.getChildren().clear();
                stackPane.getChildren().add(recipeDetailsTextArea);

                // Show the Back button
                stackPane.getChildren().add(backButton);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void goBackToRecipeList() {
        // Clear the current details and show the list of recipes
        stackPane.getChildren().clear();
        stackPane.getChildren().add(recipeListView);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



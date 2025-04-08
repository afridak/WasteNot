import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserDashboard extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome to WasteNot!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label menuLabel = new Label("Choose an option:");
        menuLabel.setStyle("-fx-font-size: 16px;");

        // Buttons for different sections
        Button inventoryButton = new Button("Inventory");
        Button recipesButton = new Button("Recipes");
        Button remindersButton = new Button("Reminders");
        Button shoppingListButton = new Button("Shopping List");
        Button statisticsButton = new Button("Statistics");

        // Set button styles
        inventoryButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        recipesButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px;");
        remindersButton.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white; -fx-font-size: 14px;");
        shoppingListButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 14px;");
        statisticsButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-size: 14px;");

        // Event Handlers (Replace these with actual pages later)
        inventoryButton.setOnAction(e -> openInventoryPage());
        recipesButton.setOnAction(e -> openRecipesPage(primaryStage));
        remindersButton.setOnAction(e -> openReminderPage(primaryStage));
        shoppingListButton.setOnAction(e -> openShoppingListPage());
        statisticsButton.setOnAction(e -> openStatisticsPage());

        // Layout
        VBox vbox = new VBox(10, welcomeLabel, menuLabel, inventoryButton, recipesButton, remindersButton, shoppingListButton, statisticsButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #F1F8E9; -fx-padding: 30px; -fx-border-radius: 10px;");

        // Scene
        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setTitle("User Dashboard - WasteNot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    // Method to open Inventory Page
    private void openInventoryPage() {
        InventoryPage inventoryPage = new InventoryPage();
        Stage inventoryStage = new Stage();
        inventoryPage.start(inventoryStage); // Open the Inventory Page
    }

    private void openRecipesPage(Stage primaryStage) {
        // Create a new stage for displaying recipes
        UserRecipes userRecipesPage = new UserRecipes();
        Stage recipeStage = new Stage();
        userRecipesPage.start(recipeStage); // Open the UserRecipes page
    }

    // ✅ Method to open the Reminder Page
    private void openReminderPage(Stage primaryStage) {
        ReminderPage reminderPage = new ReminderPage();
        Stage reminderStage = new Stage();
        reminderPage.start(reminderStage);
    }

    // Method to open the Statistics Page
    private void openStatisticsPage() {
        StatisticsPage statisticsPage = new StatisticsPage();
        Stage statisticsStage = new Stage();
        statisticsPage.start(statisticsStage); // Open the StatisticsPage
    }

    // Method to open the Shopping List Page
    private void openShoppingListPage() {
        ShoppingListPage shoppingListPage = new ShoppingListPage();
        Stage shoppingListStage = new Stage();
        shoppingListPage.start(shoppingListStage); // Open the ShoppingListPage
    }


    public static void main(String[] args) {
        launch(args);
    }
}



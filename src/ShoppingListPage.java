import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public  class ShoppingListPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Shopping List - WasteNot");

        // TextField for adding new items
        TextField itemField = new TextField();
        itemField.setPromptText("Enter an item");

        // Button to add items
        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        // ListView to display items with checkboxes
        ListView<CheckBox> shoppingListView = new ListView<>();

        // Add button event handler
        addButton.setOnAction(e -> {
            String item = itemField.getText().trim();
            if (!item.isEmpty()) {
                CheckBox checkBox = new CheckBox(item);
                shoppingListView.getItems().add(checkBox);
                itemField.clear(); // Clear the input field
            }
        });

        // Button to remove selected items
        Button removeButton = new Button("Remove Selected");
        removeButton.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white;");
        removeButton.setOnAction(e -> {
            // Remove all checked items
            shoppingListView.getItems().removeIf(CheckBox::isSelected);
        });

        // Button to edit selected item
        Button editButton = new Button("Edit Selected");
        editButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        editButton.setOnAction(e -> {
            CheckBox selectedItem = shoppingListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // Open a dialog to edit the item
                TextInputDialog dialog = new TextInputDialog(selectedItem.getText());
                dialog.setTitle("Edit Item");
                dialog.setHeaderText("Edit the selected item");
                dialog.setContentText("Item:");

                // Show the dialog and wait for user input
                dialog.showAndWait().ifPresent(newText -> {
                    if (!newText.trim().isEmpty()) {
                        selectedItem.setText(newText.trim()); // Update the item text
                    }
                });
            } else {
                // Show a warning if no item is selected
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText("No Item Selected");
                alert.setContentText("Please select an item to edit.");
                alert.showAndWait();
            }
        });

        // Layout
        HBox inputLayout = new HBox(10, itemField, addButton);
        inputLayout.setAlignment(Pos.CENTER);

        HBox buttonLayout = new HBox(10, removeButton, editButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, inputLayout, shoppingListView, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1F8E9;");

        // Scene setup
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
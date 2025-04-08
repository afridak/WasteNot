import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InventoryItem {
    private final StringProperty name;
    private final StringProperty category;
    private final StringProperty expiryDate;

    public InventoryItem(String name, String category, String expiryDate) {
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.expiryDate = new SimpleStringProperty(expiryDate);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public String getExpiryDate() {
        return expiryDate.get();
    }

    public StringProperty expiryDateProperty() {
        return expiryDate;
    }
}

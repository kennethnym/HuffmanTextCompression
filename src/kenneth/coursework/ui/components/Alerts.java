package kenneth.coursework.ui.components;

import javafx.scene.control.Alert;

public class Alerts {
    private static void show(String title, String message, Alert.AlertType type) {
        final var alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        show(title, message, Alert.AlertType.ERROR);
    }

    public static void showConfirmation(String title, String message) {
        show(title, message, Alert.AlertType.INFORMATION);
    }
}

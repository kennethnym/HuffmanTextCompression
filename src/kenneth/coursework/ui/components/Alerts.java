package kenneth.coursework.ui.components;

import javafx.scene.control.Alert;

/**
 * A util class that contains util methods to display different levels of alert dialogs.
 */
public class Alerts {
    private static void show(String title, String message, Alert.AlertType type) {
        final var alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error dialog.
     *
     * @param title   The title of the dialog
     * @param message The message of the dialog
     */
    public static void showError(String title, String message) {
        show(title, message, Alert.AlertType.ERROR);
    }

    /**
     * Shows a confirmation dialog. Can be used to confirm a certain action is complete.
     *
     * @param title   The title of the dialog
     * @param message The message of the dialog
     */
    public static void showConfirmation(String title, String message) {
        show(title, message, Alert.AlertType.INFORMATION);
    }
}

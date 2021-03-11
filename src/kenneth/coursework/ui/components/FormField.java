package kenneth.coursework.ui.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FormField extends VBox {
    private static final int LABEL_SPACING = 8;

    public FormField(String description) {
        super(LABEL_SPACING);

        final var label = new Label(description);

        getChildren().add(label);
    }

    public void setControl(Node control) {
        getChildren().add(control);
    }
}

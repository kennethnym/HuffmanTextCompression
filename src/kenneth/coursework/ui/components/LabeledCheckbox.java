package kenneth.coursework.ui.components;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class LabeledCheckbox extends HBox {
    private final CheckBox checkbox;

    public LabeledCheckbox(String label) {
        super(8);

        final var checkboxLabel = new Label(label);
        checkbox = new CheckBox();

        getChildren().addAll(checkboxLabel, checkbox);
    }

    public boolean isSelected() {
        return checkbox.isSelected();
    }
}

package kenneth.coursework.ui.components;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Creates a field for choosing a file.
 */
public class FileInputField extends FormField {
    public interface FileHandler {
        void handleFile(File pickedFile);
    }

    public enum ChooseMode {
        DIRECTORY,
        FILE
    }

    private static final long SUGGESTION_DELAY = 200;
    private static final String FILE_CHOOSER_TITLE = "Open file...";
    private static final String DIRECTORY_CHOOSER_TITLE = "Pick a folder...";

    private Timer pathSuggestionTimer;

    private TextField filePathBox;
    private ChooseMode chooseMode;
    private FileHandler fileHandler;

    private final PathSuggestionProvider pathSuggestionProvider = new PathSuggestionProvider();

    private final EventHandler<ActionEvent> pickFile = event -> {
        final var window = getScene().getWindow();
        File selectedFile;

        switch (chooseMode) {
            case FILE:
                final var fileChooser = new FileChooser();
                fileChooser.setTitle(FILE_CHOOSER_TITLE);
                selectedFile = fileChooser.showOpenDialog(window);
                break;

            case DIRECTORY:
                final var directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(DIRECTORY_CHOOSER_TITLE);
                selectedFile = directoryChooser.showDialog(window);
                break;

            default:
                selectedFile = null;
                break;
        }

        if (selectedFile != null) {
            filePathBox.setText(selectedFile.getAbsolutePath());
        }

        fileHandler.handleFile(selectedFile);
    };

    private final ChangeListener<String> filePathBoxTextListener = (__, ___, newText) -> {
        if (pathSuggestionTimer != null) {
            pathSuggestionTimer.cancel();
        }
        pathSuggestionTimer = new Timer();
        pathSuggestionTimer.schedule(new PathSuggestionGenerator(newText), SUGGESTION_DELAY);
    };

    public FileInputField(String label, FileHandler fileHandler, ChooseMode mode) {
        super(label);

        this.fileHandler = fileHandler;
        chooseMode = mode;

        filePathBox = new TextField();
        final var inputFileBoxContainer = new HBox(8);
        final var browseFileButton = new Button("Browse...");

        browseFileButton.setOnAction(pickFile);
        filePathBox.textProperty().addListener(filePathBoxTextListener);
        TextFields.bindAutoCompletion(filePathBox, pathSuggestionProvider);
        HBox.setHgrow(filePathBox, Priority.ALWAYS);
        inputFileBoxContainer.getChildren().addAll(filePathBox, browseFileButton);
        setControl(inputFileBoxContainer);
    }

    private class PathSuggestionGenerator extends TimerTask {
        private final File parent;

        private PathSuggestionGenerator(String partialPath) {
            final var currentDir = Paths.get(partialPath);
            final var parent = currentDir.getParent();
            this.parent = parent == null ? currentDir.toFile() : parent.toFile();
        }

        @Override
        public void run() {
            try {
                final var dirs = parent.listFiles(File::isDirectory);
                if (dirs != null) {
                    pathSuggestionProvider.suggestedPaths = dirs;
                }
            } catch (SecurityException ignored) { }
        }
    }

    private static class PathSuggestionProvider implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<File>> {
        private File[] suggestedPaths;

        @Override
        public Collection<File> call(AutoCompletionBinding.ISuggestionRequest param) {
            return Arrays.asList(suggestedPaths);
        }
    }
}

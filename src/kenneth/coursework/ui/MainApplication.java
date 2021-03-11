package kenneth.coursework.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kenneth.coursework.compression.HuffmanCompressor;
import kenneth.coursework.ui.components.Alerts;
import kenneth.coursework.ui.components.FileInputField;
import kenneth.coursework.ui.components.FormField;
import kenneth.coursework.ui.components.LabeledCheckbox;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private final HuffmanCompressor compressor = new HuffmanCompressor();

    private Stage primaryStage;

    private File inputFile = null;
    private File outputDirectory = null;

    private TextField outputFileNameBox;
    private LabeledCheckbox overwriteCheckbox;
    private Dialog progressDialog;

    private final FileInputField.FileHandler inputFileHandler = file -> inputFile = file;

    private final FileInputField.FileHandler outputDirHandler = file -> outputDirectory = file;

    private final EventHandler<ActionEvent> compressButtonAction = event -> beginCompression();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(drawMainContent()));
        primaryStage.setTitle("Huffman Compressor");
        primaryStage.setWidth(600);
        primaryStage.show();
    }

    private Parent drawMainContent() {
        final var vbox = new VBox(16);

        final var inputFileField = new FileInputField(
                "Path to the file you want to compress:",
                inputFileHandler,
                FileInputField.ChooseMode.FILE
        );

        final var outputDirField = new FileInputField(
                "The directory the compressed file should be in",
                outputDirHandler,
                FileInputField.ChooseMode.DIRECTORY
        );

        final var outputFileNameField = new FormField("What should the name of the compressed file be?");
        outputFileNameBox = new TextField();
        outputFileNameField.setControl(outputFileNameBox);

        final var actionButtonContainer = new HBox();
        final var compressButton = new Button("Compress");
        compressButton.setOnAction(compressButtonAction);
        actionButtonContainer.setPadding(new Insets(8, 0, 0, 0));
        actionButtonContainer.setAlignment(Pos.CENTER_RIGHT);
        actionButtonContainer.getChildren().addAll(compressButton);

        overwriteCheckbox = new LabeledCheckbox("Overwrite existing file");

        vbox.setFillWidth(true);
        vbox.setPadding(new Insets(16));
        vbox.getChildren().addAll(inputFileField, outputDirField, outputFileNameField, overwriteCheckbox, actionButtonContainer);

        return vbox;
    }

    private boolean validateInput() {
        if (inputFile == null) {
            Alerts.showError("Invalid input", "You must provide a file for compression.");
            return false;
        } else if (outputDirectory == null) {
            Alerts.showError("Invalid input", "You must select an output directory.");
            return false;
        } else if (outputFileNameBox.getText().isBlank()) {
            Alerts.showError("Invalid input", "You must provide a name for the compressed file.");
            return false;
        }
        return true;
    }

    private void beginCompression() {
        final var isInputValid = validateInput();
        if (!isInputValid) return;

        new Thread(new CompressionTask()).start();
        showProgressDialog();
    }

    private void showProgressDialog() {
        progressDialog = new Dialog();
        progressDialog.initModality(Modality.WINDOW_MODAL);
        progressDialog.initOwner(primaryStage);

        final var container = new VBox();
        final var loadingLabel = new Label("Compressing...");
        final var progressBar = new ProgressBar();
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        loadingLabel.setContentDisplay(ContentDisplay.BOTTOM);
        container.setFillWidth(true);
        loadingLabel.setGraphic(progressBar);
        container.getChildren().addAll(loadingLabel);

        progressDialog
                .getDialogPane()
                .getButtonTypes()
                .add(new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));

        progressDialog.setGraphic(container);
        progressDialog.showAndWait();
    }

    private void hideProgressDialog() {
        progressDialog.setResult(true);
        progressDialog.close();
    }

    private void showSuccess() {
        Alerts.showConfirmation("Success", "Your file has been successfully compressed.");
        hideProgressDialog();
    }

    private void showError() {
        Alerts.showError("Unable to compress file", "An error occurred when trying to compress the file.");
        hideProgressDialog();
    }

    private void showFileExistError() {
        Alerts.showError(
                "File already exists",
                "Compression operation aborted because the destination contains a file with the same file name. " +
                        "Enable the 'Overwrite' option to overwrite the file."
        );
        hideProgressDialog();
    }

    private class CompressionTask implements Runnable {
        @Override
        public void run() {
            try {
                final var outputFileName = outputFileNameBox.getText();
                final var outputFile = Paths.get(outputDirectory.getAbsolutePath(), outputFileName).toFile();
                final var shouldOverwrite = overwriteCheckbox.isSelected();

                compressor.compress(inputFile, outputFile, shouldOverwrite);

                Platform.runLater(MainApplication.this::showSuccess);
            } catch (FileAlreadyExistsException ex) {
                Platform.runLater(MainApplication.this::showFileExistError);
            } catch (IOException ex) {
                Platform.runLater(MainApplication.this::showError);
            }
        }
    }
}

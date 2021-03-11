package kenneth.coursework.ui.pages;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import kenneth.coursework.compression.HuffmanCompressor;
import kenneth.coursework.compression.HuffmanDecompressor;
import kenneth.coursework.exceptions.IncorrectFormatException;
import kenneth.coursework.ui.components.Alerts;
import kenneth.coursework.ui.components.FileInputField;
import kenneth.coursework.ui.components.FormField;
import kenneth.coursework.ui.components.LabeledCheckbox;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

public class MainPage extends VBox {
    private enum OperationMode {
        COMPRESS,
        DECOMPRESS,
    }

    private final HuffmanCompressor compressor;
    private final HuffmanDecompressor decompressor;

    private File inputFile = null;
    private File outputDirectory = null;

    private TextField outputFileNameBox;
    private LabeledCheckbox overwriteCheckbox;
    private Dialog progressDialog;
    private ToggleGroup operationModeToggleGroup;

    private final FileInputField.FileHandler inputFileHandler = file -> inputFile = file;

    private final FileInputField.FileHandler outputDirHandler = file -> outputDirectory = file;

    private final EventHandler<ActionEvent> compressButtonAction = event -> beginCompression();

    public MainPage(HuffmanCompressor huffmanCompressor, HuffmanDecompressor huffmanDecompressor) {
        super(16);

        compressor = huffmanCompressor;
        decompressor = huffmanDecompressor;

        drawMainPage();
    }

    private void drawMainPage() {
        operationModeToggleGroup = new ToggleGroup();
        final var operationModeLabel = new Label("Operation:");

        final var optionsContainer = new HBox(16);
        final var compressOption = new RadioButton("Compress");
        compressOption.setUserData(OperationMode.COMPRESS);
        compressOption.setSelected(true);
        compressOption.setToggleGroup(operationModeToggleGroup);
        final var decompressOption = new RadioButton("Decompress");
        decompressOption.setUserData(OperationMode.DECOMPRESS);
        decompressOption.setToggleGroup(operationModeToggleGroup);
        optionsContainer.getChildren().addAll(operationModeLabel, compressOption, decompressOption);

        final var inputFileField = new FileInputField(
                "Input file:",
                inputFileHandler,
                FileInputField.ChooseMode.FILE
        );

        final var outputDirField = new FileInputField(
                "The directory the output file should be in",
                outputDirHandler,
                FileInputField.ChooseMode.DIRECTORY
        );

        final var outputFileNameField = new FormField("What should the name of the output file be?");
        outputFileNameBox = new TextField();
        outputFileNameField.setControl(outputFileNameBox);

        final var actionButtonContainer = new HBox();
        final var compressButton = new Button("Compress");
        compressButton.setOnAction(compressButtonAction);
        actionButtonContainer.setPadding(new Insets(8, 0, 0, 0));
        actionButtonContainer.setAlignment(Pos.CENTER_RIGHT);
        actionButtonContainer.getChildren().addAll(compressButton);

        overwriteCheckbox = new LabeledCheckbox("Overwrite existing file");

        setFillWidth(true);
        setPadding(new Insets(16));
        getChildren().addAll(
                optionsContainer,
                inputFileField,
                outputDirField,
                outputFileNameField,
                overwriteCheckbox,
                actionButtonContainer
        );
    }

    private boolean validateInput() {
        if (inputFile == null) {
            Alerts.showError("Invalid input", "You must provide an input file.");
            return false;
        } else if (outputDirectory == null) {
            Alerts.showError("Invalid input", "You must select an output directory.");
            return false;
        } else if (outputFileNameBox.getText().isBlank()) {
            Alerts.showError("Invalid input", "You must provide a name for the output file.");
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
        progressDialog.initOwner(getScene().getWindow());

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
        Alerts.showConfirmation("Success", "Your file has been successfully processed.");
        hideProgressDialog();
    }

    private void showError() {
        Alerts.showError("Unable to complete operation.", "An error occurred when trying to process the file.");
        hideProgressDialog();
    }

    private void showFileExistError() {
        Alerts.showError(
                "File already exists",
                "Operation aborted because the destination contains a file with the same file name. " +
                        "Enable the 'Overwrite' option to overwrite the file."
        );
        hideProgressDialog();
    }

    private void showInvalidFileError() {
        Alerts.showError(
                "Invalid file",
                "The file you selected for decompression is in an incorrect format." +
                        "Make sure the file is produced by this program. It should end in '.huff'."
        );
    }

    private class CompressionTask implements Runnable {
        @Override
        public void run() {
            try {
                final var operation = (OperationMode) operationModeToggleGroup.getSelectedToggle().getUserData();
                final var outputFileName = outputFileNameBox.getText();
                final var outputFile = Paths.get(outputDirectory.getAbsolutePath(), outputFileName).toFile();
                final var shouldOverwrite = overwriteCheckbox.isSelected();

                switch (operation) {
                    case COMPRESS:
                        compressor.compress(inputFile, outputFile, shouldOverwrite);
                        break;
                    case DECOMPRESS:
                        decompressor.decompress(inputFile, outputFile, shouldOverwrite);
                        break;
                }

                Platform.runLater(MainPage.this::showSuccess);
            } catch (IncorrectFormatException ex) {
                Platform.runLater(MainPage.this::showInvalidFileError);
            } catch (FileAlreadyExistsException ex) {
                Platform.runLater(MainPage.this::showFileExistError);
            } catch (IOException ex) {
                Platform.runLater(MainPage.this::showError);
            }
        }
    }
}

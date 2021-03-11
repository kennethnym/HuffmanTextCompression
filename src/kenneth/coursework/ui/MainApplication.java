package kenneth.coursework.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kenneth.coursework.compression.HuffmanCompressor;
import kenneth.coursework.compression.HuffmanDecompressor;
import kenneth.coursework.ui.pages.MainPage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private final HuffmanCompressor compressor = new HuffmanCompressor();
    private final HuffmanDecompressor decompressor = new HuffmanDecompressor();

    @Override
    public void start(Stage primaryStage) {
        final var mainPage = new MainPage(compressor, decompressor);

        primaryStage.setScene(new Scene(mainPage));
        primaryStage.setTitle("Huffman Compressor");
        primaryStage.setWidth(600);
        primaryStage.show();
    }
}

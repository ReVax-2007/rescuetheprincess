package com.champlain.soft.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.Random;

public class Gameboard extends Application {
    // Constants used for the grid and window sizing
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 800;
    private static final int IMG_WIDTH = 80;
    private static final int IMG_HEIGHT = 80;

    // Define what can exist in each cell
    enum CellType {
        GRASS, PLAYER, PRINCESS, BOMB, WALL
    }

    // The "Matrix" stores the logical state of the game board
    private final CellType[][] matrix = new CellType[ROWS][COLS];
    
    // Member variables for resources so they are loaded once and shared
    // Note: mediaPlayer MUST be a field to prevent it from being garbage collected while playing music.
    private MediaPlayer mediaPlayer;
    private Image playerImg, princessImg, wallImg, bombImg, grassImg;

    @Override
    public void start(Stage stage) {
        // Delegate setup to specific methods to keep the start-up flow clean
        loadImages();
        setupMusic();
        initMatrix();

        GridPane grid = new GridPane();
        drawBoard(grid);

        BorderPane root = new BorderPane();
        root.setCenter(grid);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        stage.setTitle("Rescue the Princess");
        stage.setScene(scene);
        stage.show();
    }

    // Load images once from disk to prevent lag when the board redraws
    private void loadImages() {
        try {
            playerImg = new Image(String.valueOf(getClass().getResource("player.png")));
            princessImg = new Image(String.valueOf(getClass().getResource("princess.png")));
            wallImg = new Image(String.valueOf(getClass().getResource("wall.png")));
            bombImg = new Image(String.valueOf(getClass().getResource("bomb.png")));
            grassImg = new Image(String.valueOf(getClass().getResource("grass.png")));
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    // Encapsulate music logic to keep it separate from UI code
    private void setupMusic() {
        try {
            URL musicUrl = getClass().getResource("Hellwalker.mp3");
            if (musicUrl != null) {
                Media sound = new Media(musicUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setOnReady(() -> {
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    mediaPlayer.play();
                });
            } else {
                System.err.println("Music file 'Hellwalker.mp3' not found!");
            }
        } catch (Exception e) {
            System.err.println("Music error: " + e.getMessage());
        }
    }

    // Set up the initial game state in the matrix
    private void initMatrix() {
        // Fill everything with grass first
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                matrix[r][c] = CellType.GRASS;
            }
        }

        // Set starting player position
        // These are local for now as they are only used for initialization
        int playerRow = 1;
        int playerCol = 1;
        matrix[playerRow][playerCol] = CellType.PLAYER;

        Random rand = new Random();
        int princessRow, princessCol, bombRow, bombCol;

        // Randomize princess position, ensuring it's not on top of the player
        do {
            princessRow = rand.nextInt(ROWS - 2) + 1;
            princessCol = rand.nextInt(COLS - 2) + 1;
        } while (princessCol == playerCol && princessRow == playerRow);
        matrix[princessRow][princessCol] = CellType.PRINCESS;

        // Spawn a random number of bombs in random grass spots
        int numBombsToSpawn = rand.nextInt(2) + 5;
        int bombsPlaced = 0;
        do {
            bombRow = rand.nextInt(ROWS - 2) + 1;
            bombCol = rand.nextInt(COLS - 2) + 1;
            if (matrix[bombRow][bombCol] == CellType.GRASS) {
                matrix[bombRow][bombCol] = CellType.BOMB;
                bombsPlaced++;
            }
        } while (bombsPlaced < numBombsToSpawn);

        // Place wall perimeter around the board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (row == 0 || row == ROWS - 1 || col == 0 || col == COLS - 1) {
                    matrix[row][col] = CellType.WALL;
                }
            }
        }
    }

    // Translate the logical 'matrix' into visual JavaFX components
    private void drawBoard(GridPane grid) {
        grid.getChildren().clear(); // Wipe the board before redrawing

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(IMG_WIDTH, IMG_HEIGHT);
                cell.setStyle("-fx-border-color: black; -fx-background-color: beige;");

                // Determine which image to show based on the matrix state
                Image imgToDraw = grassImg;
                CellType type = matrix[row][col];
                
                if (type == CellType.PLAYER) imgToDraw = playerImg;
                else if (type == CellType.PRINCESS) imgToDraw = princessImg;
                else if (type == CellType.BOMB) imgToDraw = bombImg;
                else if (type == CellType.WALL) imgToDraw = wallImg;

                if (imgToDraw != null) {
                    ImageView iv = new ImageView(imgToDraw);
                    iv.setFitHeight(IMG_HEIGHT);
                    iv.setFitWidth(IMG_WIDTH);
                    cell.getChildren().add(iv);
                }

                grid.add(cell, col, row);
            }
        }
    }
}

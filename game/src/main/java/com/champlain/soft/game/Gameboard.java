package com.champlain.soft.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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

    Random rand = new Random();

    // Constants used for the grid and window sizing
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 800;
    private static final int IMG_WIDTH = 80;
    private static final int IMG_HEIGHT = 80;
    private final int BOMBCOUNT = rand.nextInt(5,8);
    private int lives = (BOMBCOUNT/2) + 2;

    // Define what can exist in each cell
    enum CellType {
        GRASS, PLAYER, PRINCESS, BOMB, WALL
    }

    // The "Matrix" stores the logical state of the game board
    private final CellType[][] matrix = new CellType[ROWS][COLS];
    private GridPane gameGrid;
    
    // Member variables for resources so they are loaded once and shared
    private MediaPlayer mediaPlayer;
    private Image playerImg, princessImg, wallImg, bombImg, grassImg;

    private boolean isGameOver = false;

    @Override
    public void start(Stage stage) {

        gameGrid = new GridPane();
        // Delegate setup to specific methods to keep the start-up flow clean
        loadImages();
        setupMusic();
        initMatrix();

        drawBoard(gameGrid);

        BorderPane root = new BorderPane();
        root.setCenter(gameGrid);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> movePlayer(0, -1); // Up
                case S, DOWN -> movePlayer(0, 1);  // Down
                case A, LEFT -> movePlayer(-1, 0); // Left
                case D, RIGHT -> movePlayer(1, 0);  // Right
            }
        });


        stage.setTitle("Rescue the Princess");
        stage.setScene(scene);
        stage.show();
        gameGrid.requestFocus();

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
            // 1. Create an array of music file names
            String[] tracks = {"Hellwalker.mp3", "PINKPANTHER.mp3"};

            // 2. Use Random to pick an index (0 or 1)
            int randomIndex = new Random().nextInt(tracks.length);
            String selectedTrack = tracks[randomIndex];

            URL musicUrl = getClass().getResource(selectedTrack);

            if (musicUrl != null) {
                Media sound = new Media(musicUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setOnReady(() -> {
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    mediaPlayer.play();
                });
                // Optional: Log which track was chosen
                System.out.println("Now playing: " + selectedTrack);
            } else {
                System.err.println("Music file '" + selectedTrack + "' not found!");
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
        this.playerRow = 1;
        this.playerCol = 1;
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
//        int numBombsToSpawn = rand.nextInt(2) + 5;
        int bombsPlaced = 0;
        do {
            bombRow = rand.nextInt(ROWS - 2) + 1;
            bombCol = rand.nextInt(COLS - 2) + 1;
            if (matrix[bombRow][bombCol] == CellType.GRASS) {
                matrix[bombRow][bombCol] = CellType.BOMB;
                bombsPlaced++;
            }
        } while (bombsPlaced < BOMBCOUNT);

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
                else if (type == CellType.BOMB) imgToDraw = grassImg; //bombImg
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
    private int playerRow = 0, playerCol = 0;
    private void movePlayer(int deltaCol, int deltaRow) {
        if (isGameOver) return;
        int newRow = playerRow + deltaRow;
        int newCol = playerCol + deltaCol;

        if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
            CellType target = matrix[newRow][newCol];

            if (target == CellType.WALL) {
                return; // Do nothing, it's a wall
            }
            if (target == CellType.BOMB) {
                lives--;
                if (lives > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("BOOM!!");
                    alert.setHeaderText(null);
                    alert.setContentText("You Hit a BOMB. Lives left: " + lives);
                    alert.showAndWait();

                } else {
                    gameOver();
                }
                return;
            }

            if (target == CellType.PRINCESS) {
                winGame();
                System.out.println("Victory! You rescued the Princess!");
                return;
            }

            // Standard movement logic
            matrix[playerRow][playerCol] = CellType.GRASS;
            playerRow = newRow;
            playerCol = newCol;
            matrix[playerRow][playerCol] = CellType.PLAYER;

            drawBoard(gameGrid);
        }
    }
    private void gameOver() {
        if (isGameOver) return; // Exit if we already ran this once
        isGameOver = true;

        StackPane screen = new StackPane();
        screen.setStyle("-fx-background-color: red;");
        Label message = new Label("Game Over 💣");
        message.setStyle("-fx-font-size: 60px; -fx-text-fill: white; -fx-font-weight: bold;");

        screen.getChildren().add(message);

        gameGrid.getScene().setRoot(screen);
    }
    private void winGame() {
        if (isGameOver) return;
        isGameOver = true;

        StackPane screen = new StackPane();
        screen.setStyle("-fx-background-color: green;");

        Label message = new Label("YOU RESCUED THE PRINCESS! 👑\n\n\nLives Left: " + lives + "\n\n\nBombs On Board: " + BOMBCOUNT);
        message.setStyle("-fx-font-size: 52px; -fx-text-fill: white; -fx-font-weight: bold;");

        screen.getChildren().add(message);

        // Swap the root of the scene
        gameGrid.getScene().setRoot(screen);
    }
}

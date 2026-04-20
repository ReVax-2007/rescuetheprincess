package com.champlain.soft.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gameboard extends Application {
    // 🔹 Grid constants
    private static final int ROWS = 10;
    private static final int COLS = 10;

    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 800;
    enum CellType {
        GRASS, PLAYER, PRINCESS, BOMB, WALL
    }

    // 🔹 Use "matrix" instead of "map"
    private CellType[][] matrix = new CellType[ROWS][COLS];

    @Override
    public void start(Stage stage) {

        initMatrix();

        GridPane grid = new GridPane();
        drawBoard(grid);

        BorderPane root = new BorderPane();
        root.setCenter(grid);

        Scene scene = new Scene(root, SCENE_HEIGHT,SCENE_WIDTH);

        stage.setTitle("Rescue the Princess");
        stage.setScene(scene);
        stage.show();
    }

    private void initMatrix() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                matrix[r][c] = CellType.GRASS;
            }
        }

        // Sample objects
        matrix[0][0] = CellType.PLAYER;
        matrix[9][9] = CellType.PRINCESS;
        matrix[4][5] = CellType.BOMB;
        matrix[1][1] = CellType.WALL;
        matrix[1][2] = CellType.WALL;
    }

    private void drawBoard(GridPane grid) {
        grid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                StackPane cell = new StackPane();
                cell.setPrefSize(SCENE_HEIGHT, SCENE_WIDTH);
                cell.setStyle("-fx-border-color: black; -fx-background-color: beige;");

                Label label = new Label();

                if(matrix[row][col] == CellType.PLAYER ) {
                    label.setText("🧍");
                }else if(matrix[row][col] == CellType.PRINCESS ) {
                    label.setText("👸");
                }else if(matrix[row][col] == CellType.BOMB){
                    label.setText("💣");
                }else if(matrix[row][col] == CellType.WALL){
                    label.setText("");
                    cell.setStyle("-fx-border-color: black; -fx-background-color: gray;");
                }else{
                    label.setText("");
                }

                cell.getChildren().add(label);
                grid.add(cell, col, row);
            }
        }
    }


}
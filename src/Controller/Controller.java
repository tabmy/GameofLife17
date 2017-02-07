package Controller;

import Model.Board;
import Model.StaticBoard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Button startButton;
    @FXML
    public Canvas playArea;
    private GraphicsContext gc;

    private Board gameBoard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = playArea.getGraphicsContext2D();
        gameBoard = new StaticBoard();
    }


    @FXML
    public void draw(){
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0,0,playArea.getWidth(), playArea.getHeight());

        gc.setFill(Color.BLACK);

        for (int i = 0; i < 200 ; i++) {
            for (int j = 0; j < 200; j++) {
                if (gameBoard.getCellState(i,j) == 0){
                    gc.fillRect(i * 3, j * 3, 2,2);
                }
            }
        }
    }

    @FXML
    public void clearBoard(){
        gc.clearRect(0,0, playArea.getWidth(), playArea.getHeight());
    }

}

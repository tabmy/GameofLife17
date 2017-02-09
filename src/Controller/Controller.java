package Controller;

import Model.Board;
import Model.StaticBoard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Button startButton;
    @FXML
    public Canvas playArea;
    private GraphicsContext gc;
    @FXML
    public Slider numCellsSlider;
    private Board gameBoard;

    @FXML
    public void changeCellState(MouseEvent e){
        double x = e.getX() / gameBoard.getCellSize();
        double y = e.getY() / gameBoard.getCellSize();

        if (e.getButton() == MouseButton.PRIMARY) {
            int cS = gameBoard.getCellState((int)x, (int)y);
            gameBoard.setCellState((int)x, (int)y, (byte)Math.abs(cS - 1));
        }

        draw();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = playArea.getGraphicsContext2D();
        gameBoard = new StaticBoard();
        draw();
    }

    @FXML
    public void changeCellSize(){
        gameBoard.setCellSize((int)numCellsSlider.getValue());
        draw();
    }

    @FXML
    public void draw(){
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0,0,playArea.getWidth(), playArea.getHeight());

        gc.setFill(Color.BLACK);

        int cS = gameBoard.getCellSize();

        for (int i = 0; i < gameBoard.getHEIGHT() ; i++) {
            for (int j = 0; j < gameBoard.getWIDTH(); j++) {
                if (gameBoard.getCellState(i,j) == 0){
                    gc.fillRect(i * cS, j * cS, cS,cS);
                }
            }
        }
    }

    @FXML
    public void clearBoard(){
        gc.clearRect(0,0, playArea.getWidth(), playArea.getHeight());
    }

}

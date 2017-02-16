package Controller;

import Model.Board;
import Model.StaticBoard;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML public Button startButton;
    @FXML public Button animBtn;
    @FXML public Button clearButton;
    @FXML public Canvas playArea;
    @FXML public Slider cellSizeSlider;
    @FXML public Slider speedSlider;
    @FXML public Label speedInd;
    @FXML public ColorPicker backColorPicker;
    @FXML public ColorPicker cellColorPicker;
    private GraphicsContext gc;
    private final Timeline TIMELINE = new Timeline();
    private Board gameBoard;
    private AnimationTimer animationTimer;
    private boolean gameStarted;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameBoard = new StaticBoard();
        gc = playArea.getGraphicsContext2D();
        initAnimation();
        guiSetup();
        draw();
    }

    private void guiSetup(){
        speedSlider.setValue(1);
        setTimelineRate();
        cellSizeSlider.setValue(20);
        changeCellSize();
        cellColorPicker.setValue(Color.BLACK);
    }

    private void initAnimation() {
        Duration duration = new Duration(1000);
        KeyFrame keyFrame = new KeyFrame(duration, (ActionEvent) -> {gameBoard.nextGeneration(); drawCells();});

        TIMELINE.setCycleCount(Timeline.INDEFINITE);
        TIMELINE.getKeyFrames().add(keyFrame);

        animationTimer = new AnimationTimer() {

            @Override
            public void handle(long now) { }
        };
    }

    @FXML
    public void handleAnimation() {
        if (TIMELINE.getStatus() == Animation.Status.RUNNING) {
            TIMELINE.stop();
            animationTimer.stop();
        }
       else if (TIMELINE.getStatus() == Animation.Status.STOPPED) {
            setTimelineRate();
            TIMELINE.play();
            animationTimer.start();
        }
    }

    @FXML
    public void setTimelineRate() {
        TIMELINE.setRate(speedSlider.getValue());
        speedInd.setText(String.format("%s: %.2f", "Speed", speedSlider.getValue()));

    }

    @FXML
    public void changeCellState(MouseEvent e){
        double x = e.getX() / gameBoard.getCellSize();
        double y = e.getY() / gameBoard.getCellSize();

        if (e.getButton() == MouseButton.PRIMARY) {
            int cS = gameBoard.getCellState((int)x, (int)y);
            gameBoard.setCellState((int)x, (int)y, (byte)Math.abs(cS - 1));
        }

        drawCells();
    }


    @FXML
    public void changeCellSize() {
        gameBoard.setCellSize(cellSizeSlider.getValue());
        draw();

    }

    @FXML
    public void draw(){
        drawBackground();
        drawCells();
    }

    private void drawCells() {
        gc.setFill(backColorPicker.getValue());
        gc.fillRect(0,0,playArea.getWidth(), playArea.getHeight());
        gc.setFill(cellColorPicker.getValue());

        double cS = gameBoard.getCellSize();

        for (int i = 0; i < gameBoard.getHEIGHT(); i++) {
            for (int j = 0; j < gameBoard.getWIDTH(); j++) {
                if (gameBoard.getCellState(i, j) == 0) {
                    gc.fillRect(i * cS, j * cS, cS, cS);
                }
            }
        }
    }

    private void drawBackground(){
        gc.setFill(backColorPicker.getValue());
        gc.fillRect(0, 0, playArea.getWidth(), playArea.getHeight());
    }

    @FXML
    public void clearBoard() {
        gc.clearRect(0,0, playArea.getWidth(), playArea.getHeight());
    }


    @FXML public void colorChange() {
        draw();
    }

    @FXML public void exitApplication() {
        System.exit(0);
    }
}
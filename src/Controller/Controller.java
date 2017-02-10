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
    @FXML public Button clearButton;
    @FXML public Canvas playArea;
    @FXML public Slider numCellsSlider;
    @FXML public Slider speedSlider;
    @FXML public Label speedInd;
    @FXML public ColorPicker backColorPicker;
    @FXML public ColorPicker cellColorPicker;
    private final Timeline TIMELINE = new Timeline();
    private Board gameBoard;
    private AnimationTimer animationTimer;
    private GraphicsContext gc;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = playArea.getGraphicsContext2D();
        gameBoard = new StaticBoard();
        initAnimation();
        speedInd.setText("Speed: 0.00");
    }

    private void initAnimation() {
        Duration duration = new Duration(1000);
        KeyFrame keyFrame = new KeyFrame(duration, (ActionEvent) -> {gameBoard.nextGeneration();draw();});

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

    @FXML public void colorStuff() {
        gc.setFill(backColorPicker.getValue());
    }

    @FXML public void exitApplication() {
        System.exit(0);
    }
}
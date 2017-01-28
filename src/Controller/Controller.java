package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML public Canvas playArea;
    public GraphicsContext gc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = playArea.getGraphicsContext2D();
    }


    @FXML
    public void draw(){
        gc.setFill(Color.RED);
        gc.fillRect(0,0,playArea.getWidth(), playArea.getHeight());
    }

}

package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML public Canvas playArea;
    public GraphicsContext gc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

package Controller;

import Model.*;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * {@code Controller} is a class that handles the functionality of all the components in the graphical user interface
 * of Game Of Life. It implements the {@code Initializable} interface and overrides the method {@code Initialize()}
 * from it.
 * Every customizable aspect of the game is handled by this class; it provides different tools that allow the users to
 * set up the game according to their preferences. This mainly applies to the pattern of the cells.
 *
 * @author Branislav Petrovic
 * @author Tommy Abelsen
 * @version 1.0
 * @see javafx.fxml.Initializable
 * @since 20.01.2017
 */

public class Controller implements Initializable {

    /**
     * Start/stop button for the animation.
     */
    @FXML
    public Button animBtn;

    /**
     * Button that clears the canvas.
     */
    @FXML
    public Button clearButton;

    /**
     * Main canvas that provides the space for the board.
     */
    @FXML
    public Canvas playArea;

    /**
     * Slider for altering the size of the cells.
     */
    @FXML
    public Slider cellSizeSlider;

    @FXML
    public Label sizeInd;

    /**
     * Slider for altering the speed of the animation.
     */
    @FXML
    public Slider speedSlider;

    /**
     * Label that indicates the speed of the animation.
     */
    @FXML
    public Label speedInd;

    /**
     * Label that indicates the current selected shape.
     */
    @FXML
    public Label shapeLabel;
    @FXML
    private Label authorLabel;

    /**
     * Color picker for selecting the canvas background color.
     */
    @FXML
    public ColorPicker backColorPicker;

    /**
     * Color picker for selecting the color of the cells.
     */
    @FXML
    public ColorPicker cellColorPicker;

    /**
     * The graphics context of {@code playArea}.
     */
    private GraphicsContext gc;

    /**
     * {@code Timeline} used to specify the animation.
     */
    private final Timeline TIMELINE = new Timeline();

    /**
     * The board used to draw the cells.
     *
     * @see Model.Board
     */
    private Board gameBoard;

    /**
     * The timer that handles the game's animation.
     */
    private AnimationTimer animationTimer;

    private byte[][] loadBoard;

    private TextInputDialog textInputDialog = new TextInputDialog("");

    /**
     * Method {@code Initialize()} sets up the application for running. It creates a new board, where the game will
     * take place. It also initializes the main canvas and calls other key methods.
     *
     * @see Model.StaticBoard
     * @see javafx.scene.canvas.Canvas
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameBoard = new StaticBoard(
                (int) (playArea.getHeight() / cellSizeSlider.getMin()), (int) (playArea.getWidth()
                / cellSizeSlider.getMin())
        );
        gc = playArea.getGraphicsContext2D();

        // call appropriate setup methods
        initAnimation();
        guiSetup();
        draw();
        clearMetaLabels();

        //Todo remove
        System.out.printf("Height: %d\nWidth: %d", gameBoard.getHEIGHT(), gameBoard.getWIDTH());
    }

    /**
     * Provides initial values for the elements that allow user interaction. The method calls methods
     * {@code setTimelineRate()} and {@code changeCellSize()}, which set the speed of the animation and the size of the
     * cells to the values of their corresponding sliders. The values are set up so that a basic animation will run.
     * The users can later change these values as they like.
     *
     * @see javafx.scene.control.Slider
     * @see javafx.scene.control.ColorPicker
     */
    private void guiSetup() {
        speedSlider.setValue(1);
        setTimelineRate();
        cellSizeSlider.setValue(15);
        changeCellSize();
        cellColorPicker.setValue(Color.BLACK);
    }

    /**
     * Provides the initial animation for the application using a fixed time duration handled by a keyframe. When the
     * keyframe finishes, the {@code nextGeneration()} method of the game board is called. The keyframes generated
     * get set into a timeline, which again runs indefinitely (or until the user pauses the animation).
     *
     * @see javafx.util.Duration
     * @see javafx.animation.KeyFrame
     * @see javafx.animation.Timeline
     */
    private void initAnimation() {
        Duration duration = new Duration(1000);

        // call nextGeneration after each keyframe
        KeyFrame keyFrame = new KeyFrame(duration, (ActionEvent) -> {
            gameBoard.nextGeneration();
            draw();
        });

        // make the timeline run indefinitely by default
        TIMELINE.setCycleCount(Timeline.INDEFINITE);
        TIMELINE.getKeyFrames().add(keyFrame);

        animationTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
            }
        };
    }

    @FXML
    private void nextGen() {
        gameBoard.nextGeneration();
        draw();
    }

    /**
     * Controls the animation of the game. This method is linked to the Start/Stop button in the GUI. If the animation
     * is running, the animation should stop, otherwise it should start playing.
     */
    @FXML
    public void handleAnimation() {
        // stop animation if running
        if (TIMELINE.getStatus() == Animation.Status.RUNNING) {
            TIMELINE.stop();
            animationTimer.stop();
            animBtn.setText("Start");
        }
        // start animation if stopped
        else if (TIMELINE.getStatus() == Animation.Status.STOPPED) {
            setTimelineRate();
            TIMELINE.play();
            animationTimer.start();
            animBtn.setText("Stop");
        }
    }

    /**
     * Sets the value for the timeline of the animation. The value is determined by the value of the slider dedicated
     * for the animation speed, which is defined by the user. The method also indicates the current animation speed
     * at a given time.
     */
    @FXML
    public void setTimelineRate() {
        TIMELINE.setRate(speedSlider.getValue());
        speedInd.setText(String.format("%s: %.2f", "Speed", speedSlider.getValue()));
    }

    /**
     * Method {@code changeCellState} allows the users to define their own cell pattern by drawing with the mouse on
     * the screen. If a given cell is dead, clicking or dragging the mouse over it will make it come to life. Whether a
     * cell is in a legal position on the {@code gameBoard} is determined by the {@code indexCheck()} method. In the
     * end, the cells are drawn normally by the {@code draw()} method.
     *
     * @param e         The mouse event that triggers the method
     * @param mouseDrag Used to determine whether the mouse was clicked or dragged
     * @see javafx.scene.input.MouseEvent
     */
    private void changeCellState(MouseEvent e, boolean mouseDrag) {
        // determine x- and y-coordinates of the mouse event on screen
        int x = (int) Math.ceil((e.getX() / gameBoard.getCellSize())) - 1;
        int y = (int) Math.ceil((e.getY() / gameBoard.getCellSize())) - 1;

        if (e.getButton() == MouseButton.PRIMARY && indexCheck(x, y)) {
            // get the state of the clicked cell
            boolean cS = gameBoard.getCellState(x, y);

            // if the mouse was dragged, draw cells along the mouse click
            if (mouseDrag) {
                gameBoard.setCellState(x, y, true);
            } else {
                gameBoard.setCellState(x, y, !cS);
            }
        }

        // draw the updated board normally
        draw();
    }

    /**
     * Methods {@code cellDrag()} and {@code cellClick()} are implemented in the {@code Canvas} of the FXML file. They
     * give the correct mousedrag parameters to method {@code changeCellState}, depending on if the user clicked or
     * dragged on the screen.
     *
     * @param e Mouse events captured when user clicks on screen
     */
    @FXML
    public void cellDrag(MouseEvent e) {
        changeCellState(e, true);
    }

    @FXML
    public void cellClick(MouseEvent e) {
        changeCellState(e, false);
    }

    /**
     * Checks whether a cell upon which the method is called is in a legal position on the board. The indices used to
     * check the position of the cell are provided by the x and y parameters.
     *
     * @param x x-position of the cell on the board
     * @param y y-position of the cell on the board
     * @return true if the cell is in a legal position
     * false otherwise
     */
    private boolean indexCheck(int x, int y) {
        // checks whether the cell is within the width and height of the board
        return !(x < 0 || y < 0 || x >= gameBoard.getWIDTH() || y >= gameBoard.getHEIGHT());
    }

    /**
     * Changes the size of the cells. The user is allowed to change the size of the cells by altering the value of the
     * slider that determines the cell size. In order for the cells to appear after their size has been changed,
     * method {@code draw()} must be called.
     */
    @FXML
    public void changeCellSize() {
        gameBoard.setCellSize(cellSizeSlider.getValue());
        sizeInd.setText(String.format("%s : %d", "Cell Size", (int) cellSizeSlider.getValue()));
        draw();
    }

    /**
     * Method {@code draw()} is the main graphical method in the application. It calls methods {@code drawBackground()}
     * and {@code drawCells()}, which set the color of the canvas and the cells, respectively.
     */
    private void draw() {
        drawBackground();
        drawCells();
        drawGrid();
    }

    /**
     * For drawing the cells onto the board, method {@code drawCells()} gets the color value that the user has
     * specified for the cells, then iterates through the game board to check which of the cells are alive or dead. It
     * then colors the ones that are alive.
     */
    private void drawCells() {
        // get the value of the cell color picker
        gc.setFill(cellColorPicker.getValue());

        // get the size of the cells
        double cS = gameBoard.getCellSize();

        // iterate through the height and width of the board
        for (int i = 0; i < gameBoard.getWIDTH(); i++) {
            for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                // check if a given cell is alive and color it
                if (gameBoard.getCellState(i, j)) {
                    gc.fillRect((i * cS) + 0.25, (j * cS) + .25, cS - .5, cS - .5);
                }
            }
        }
    }

    /**
     * Colors the underlying canvas of the game. The color is registered from the value of the color picker that
     * specifies the canvas color.
     */
    private void drawBackground() {
        // get the value of the background color picker
        gc.setFill(backColorPicker.getValue());

        // color the background
        gc.fillRect(0, 0, playArea.getWidth(), playArea.getHeight());
    }

    private void drawGrid() {
        gc.setStroke(cellColorPicker.getValue());
        gc.setLineWidth(0.1);
        int width = gameBoard.getWIDTH();
        int height = gameBoard.getHEIGHT();
        double cS = cellSizeSlider.getValue();
        for (int i = 0; i < width; i++) {
            gc.strokeLine(i * cS, 0.25, i * cS + 0.25, height * cS + 0.25);
        }
        for (int i = 0; i < height; i++) {
            gc.strokeLine(0.25, i * cS + 0.25, width * cS + 0.25, i * cS + 0.25);
        }
    }

    /**
     * Clears the cells and the canvas from the screen.
     */
    @FXML
    public void clearBoard() {
        // assign a blank board to gameBoard
        gameBoard.clear();
        handleAnimation();

        // clear the canvas
        gc.clearRect(0, 0, playArea.getWidth(), playArea.getHeight());

        clearMetaLabels();
        draw();
    }

    /**
     * Triggers when the user has selected a new color for the cells or for the background, then assign the color
     * using method {@code draw()}.
     */
    @FXML
    public void colorChange() {
        draw();
    }

    /**
     * The following 7 methods implement the available starting patterns for the game. For each pattern/shape, the
     * indicator label tells the user which pattern is selected at a given time.
     */
    @FXML
    public void glider() {
        loadPattern("resources/glider.rle");
    }

    @FXML
    public void smallExploder() {
        shapeLabel.setText("Small Exploder");
    }

    @FXML
    public void exploder() {
        shapeLabel.setText("Exploder");
    }

    @FXML
    public void tenCellRow() {
        shapeLabel.setText("10 Cell Row");
    }

    @FXML
    public void lghtwghtSpaceship() {
        loadPattern("resources/lwss.rle");
    }

    @FXML
    public void tumbler() {
        loadPattern("resources/tumbler.rle");
    }

    @FXML
    public void gliderGun() {
        loadPattern("rerources/gosperglidergun.rle");
    }

    // IO-methods

    @FXML
    private void loadFileDisk() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Cell patterns", "*.cells", "*.rle"));
            File selectedFile = fileChooser.showOpenDialog(null);

            TIMELINE.stop();
            animationTimer.stop();
            animBtn.setText("Start");

            if (selectedFile != null) {
                loadBoard = FileHandler.readFromDisk(selectedFile);
                if (loadBoard.length > gameBoard.getWIDTH() || loadBoard[0].length > gameBoard.getHEIGHT())
                    throw new PatternFormatException("Pattern size too large for board!");

                setPattern();
            }
        } catch (IOException ex) {
            alert.setHeaderText("Something went wrong!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();

        } catch (PatternFormatException ex) {
            System.out.println(ex.getMessage());
            alert.setHeaderText("Pattern error!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        draw();
    }

    @FXML
    private void loadFileNet() {
        textInputDialog.setTitle("Load file from URL");
        textInputDialog.setHeaderText("Enter URL to GoL file");
        textInputDialog.showAndWait();

        Alert alert = new Alert(Alert.AlertType.ERROR);

        String input = textInputDialog.getResult();
        textInputDialog.getEditor().setText("");

        if (!(input == null))
            try {
                TIMELINE.stop();
                animationTimer.stop();
                animBtn.setText("Start");

                loadBoard = FileHandler.readFromURL(input);
                if (loadBoard.length > gameBoard.getWIDTH() || loadBoard[0].length > gameBoard.getHEIGHT())
                    throw new PatternFormatException("Pattern size too large for board!");

                setPattern();

            } catch (PatternFormatException pfe) {
                alert.setHeaderText("Pattern error!");
                alert.setContentText(pfe.getMessage());
                alert.showAndWait();

            } catch (IOException ioe) {
                alert.setHeaderText("Something went wrong");
                alert.setContentText("Please try again with a correct URL!");
                alert.showAndWait();

            }
        draw();
    }

    /*
    public void moveGameBoard() {
        throw new UnsupportedOperationException();
    }
    */

    private void setPattern() {
        for (int i = 0; i < loadBoard.length; i++) {
            for (int j = 0; j < loadBoard[0].length; j++) {
                gameBoard.setCellState(i, j, loadBoard[i][j] == 1);
            }
        }
        gameBoard.setCellSize(Math.floor(cellSizeSlider.getValue()));
        readMeta();
        draw();
    }

    private void readMeta() {
        ArrayList<String> meta = FileHandler.getMeta();
        shapeLabel.setText(meta.get(0) != null ? meta.get(0) : "No info...");
        authorLabel.setText(meta.get(1) != null ? meta.get(1) : "No info...");
    }

    private void clearMetaLabels() {
        String s = "No info...";

        shapeLabel.setText(s);
        authorLabel.setText(s);
    }

    private void loadPattern(String resource){
        try {
            loadBoard = FileHandler.readFromDisk(new File(resource));
            setPattern();
        } catch (IOException e) {
            System.out.println("Something went wrong...");
            System.out.println(e.getMessage());
        } catch (PatternFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Quits the application safely.
     */
    @FXML
    public void exitApplication() {
        System.exit(0);
    }

    @FXML
    private void randomize(KeyEvent e) {
        // Easter egg
        if (e.getCode().toString().toLowerCase().equals("r")) {
            backColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
            cellColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
            draw();
        } else if (e.getCode().toString().toLowerCase().equals("d")) {
            backColorPicker.setValue(Color.WHITE);
            cellColorPicker.setValue(Color.BLACK);
            draw();
        }
    }

    @FXML
    public void newTest(){
        DynamicBoard dynamicBoard = new DynamicBoard();
        gameBoard = dynamicBoard;
        //System.out.println(dynamicBoard.toStringBoard());
    }
}
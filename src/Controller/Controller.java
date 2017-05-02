package Controller;

import Model.*;
import javafx.animation.Animation;
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

    private byte[][] loadBoard;

    private TextInputDialog textInputDialog = new TextInputDialog("");

    /**
     * Method {@code Initialize()} sets up the application for running. It creates a new board, where the game will
     * take place. It also initializes the main canvas and calls other key methods.
     *
     * @see Model.Board
     * @see javafx.scene.canvas.Canvas
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameBoard = /*new StaticBoard(
                (int) (playArea.getHeight() / cellSizeSlider.getMin()), (int) (playArea.getWidth()
                / cellSizeSlider.getMin())
        ); */
                new DynamicBoard();
        gc = playArea.getGraphicsContext2D();

        // call appropriate setup methods
        initAnimation();
        guiSetup();
        draw();
        clearMetaLabels();
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
        cellSizeSlider.setValue(5);
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

            ((DynamicBoard) gameBoard).nextGenerationConcurrent();
            //gameBoard.nextGeneration();
            draw();
        });

        // make the timeline run indefinitely by default
        TIMELINE.setCycleCount(Timeline.INDEFINITE);
        TIMELINE.getKeyFrames().add(keyFrame);
    }

    @FXML
    private void nextGen() {
       // ((DynamicBoard)gameBoard).nextGenerationConcurrentPrintPerformance();
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
            animBtn.setText("Start");
        }
        // start animation if stopped
        else if (TIMELINE.getStatus() == Animation.Status.STOPPED) {
            setTimelineRate();
            TIMELINE.play();
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
        TIMELINE.setRate(speedSlider.valueProperty().intValue());
        speedInd.setText(String.format("%s: %d\n%s", "Speed", speedSlider.valueProperty().intValue(), "Generations " +
                "per " +
                "second"));
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
        // determine x - and y-coordinates of the mouse event on screen
        int x = (int) Math.ceil((e.getX() / gameBoard.getCellSize())) - 1;
        int y = (int) Math.ceil((e.getY() / gameBoard.getCellSize())) - 1;

        if (e.getButton() == MouseButton.PRIMARY && indexCheck(x, y)) {

            // if the mouse was dragged, draw cells along the mouse click
            if (mouseDrag) {
                gameBoard.setCellState(x, y, true);
            } else {
                gameBoard.setCellState(x, y, !(gameBoard.getCellState(x, y)));
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

        /*return !(x < 0 || y < 0) &&!((gameBoard instanceof StaticBoard) && (x < 0 || y < 0 || x >= gameBoard.getWIDTH
                () || y >= gameBoard
                .getHEIGHT()));*/
        return !(x < 0 || y < 0);
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
        drawGrid();
        drawCells();
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
                    gc.fillRect((i * cS) + .25, (j * cS) + .25, cS - .5, cS - .5);
                }
            }
        }
    }

    @FXML
    public void handleKeyEvents(KeyEvent event) {
        boolean[][] temp = new boolean[gameBoard.getWIDTH() + 1][gameBoard.getHEIGHT() + 1];

        switch (event.getCode().toString().toLowerCase()) {
            case "w":
                // iterate through the height and width of the board
                        /*for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                            for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                                // check if a given cell is alive and color it
                                if (gameBoard.getCellState(i, j)) {
                                    gc.fillRect((i * cS) + .25, (j * cS) + .25 - 1, cS - .5, cS - .5);
                                }
                            }
                        }*/
//                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
//                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
//                        if (gameBoard.getCellState(i, j)) {
//                            gameBoard.setCellState(i, j - 1, true);
//                            gameBoard.setCellState(i, j, false);
//                        }
//                    }
//                }
//                draw();

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (gameBoard.getCellState(i, j))
                            temp[i][j - 1] = true;
                    }
                }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        gameBoard.setCellState(i, j, false);
                    }
                }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (temp[i][j])
                            gameBoard.setCellState(i, j, true);
                    }
                }

                draw();
                break;
            case "d":
                // iterate through the height and width of the board
                        /*for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                            for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                                // check if a given cell is alive and color it
                                if (gameBoard.getCellState(i, j)) {
                                    gc.fillRect((i * cS) + .25 + 1, (j * cS) + .25, cS - .5, cS - .5);
                                }
                            }
                        }*/


                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (gameBoard.getCellState(i, j))
                            temp[i + 1][j] = true;
                        }
                    }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        gameBoard.setCellState(i, j, false);
                    }
                }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (temp[i][j])
                            gameBoard.setCellState(i, j, true);
                        }
                    }

                draw();
                break;
            case "s":
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (gameBoard.getCellState(i, j))
                            temp[i][j + 1] = true;
                    }
                }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        gameBoard.setCellState(i, j, false);
                    }
                }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (temp[i][j])
                            gameBoard.setCellState(i, j, true);
                    }
                }

                draw();
                break;

                // iterate through the height and width of the board
                        /*for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                            for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                                // check if a given cell is alive and color it
                                if (gameBoard.getCellState(i, j)) {
                                    gc.fillRect((i * cS) + .25, (j * cS) + .25 + 1, cS - .5, cS - .5);
                                }
                            }
                        }*/
//                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
//                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
//                        if (gameBoard.getCellState(i, j)) {
//                            gameBoard.setCellState(i, j + 1, true);
//                            gameBoard.setCellState(i, j, false);
//                        }
//                    }
//                }
//                draw();
            case "a":
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (gameBoard.getCellState(i, j))
                            temp[i - 1][j] = true;
                    }
                }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        gameBoard.setCellState(i, j, false);
                    }
                }

                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (temp[i][j])
                            gameBoard.setCellState(i, j, true);
                    }
                }

                draw();
                break;
                // iterate through the height and width of the board
                        /*for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                            for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                                // check if a given cell is alive and color it
                                if (gameBoard.getCellState(i, j)) {
                                    gc.fillRect((i * cS) + .25 - 1, (j * cS) + .25, cS - .5, cS - .5);
                                }
                            }
                        }*/
//                draw();
//                event.consume();
            case "r":
                backColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
                cellColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
                draw();
                break;

            case "b":
                backColorPicker.setValue(Color.WHITE);
                cellColorPicker.setValue(Color.BLACK);
                draw();
                break;
        }

        // Easter egg
//        if (event.getCode().toString().toLowerCase().equals("r")) {
//            backColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
//            cellColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
//            draw();
//        } else if (event.getCode().toString().toLowerCase().equals("b")) {
//            backColorPicker.setValue(Color.WHITE);
//            cellColorPicker.setValue(Color.BLACK);
//            draw();
//        }

//        if (movingCells) {
//            double cS = gameBoard.getCellSize();
//            int mouseX = (int) event.getX();
//            int mouseY = (int) event.getY();

//            for (int i = 0; i < gameBoard.getWIDTH(); i++) {
//                for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
//                    if (gameBoard.getCellState(i, j)) {
//                        gc.fillRect(mouseX, mouseY, cS, cS);
//                        draw();
//                    }
//                }
//            }
//        }

        // iterate through the height and width of the board
        /*for (int i = 0; i < gameBoard.getWIDTH(); i++) {
            for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                // check if a given cell is alive and color it
                if (gameBoard.getCellState(i, j)) {
                    gc.fillRect((i * cS) + .25 - cS, (j * cS) + .25, cS - .5, cS - .5);
                }
            }
        }*/

//        EventHandler<KeyEvent> keyEventEventHandler = new EventHandler<KeyEvent>() {
//            // get the size of the cells
//            double cS = gameBoard.getCellSize();
//
//            @Override
//            public void handle(KeyEvent zika) {
//
//            }
//        };
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

        int width = gameBoard.getWIDTH();// (int)playArea.getWidth();
        int height = gameBoard.getHEIGHT();// (int)playArea.getHeight();
        double cS = cellSizeSlider.getValue();

        gc.setStroke(cellColorPicker.getValue());
        gc.setLineWidth(0.1);

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

        // clear the meta information and canvas
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
     * The following 4 methods implement the available starting patterns for the game. For each pattern/shape, the
     * indicator label tells the user which pattern is selected at a given time.
     */
    @FXML
    public void glider() {
        loadPattern("resources/glider.rle");
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
        loadPattern("resources/gosperglidergun.rle");
    }

    // IO-methods

    @FXML
    private void loadFileDisk() {
        TIMELINE.stop();
        animBtn.setText("Start");
        Alert alert = new Alert(Alert.AlertType.ERROR);

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Cell patterns", "*.cells", "*.rle"));
            File selectedFile = fileChooser.showOpenDialog(null);


            if (selectedFile != null) {
                loadBoard = FileHandler.readFromDisk(selectedFile);
                /*if (gameBoard instanceof StaticBoard && (loadBoard.length > gameBoard.getWIDTH() || loadBoard[0]
                        .length >
                        gameBoard.getHEIGHT
                                ()))
                {
                    throw new PatternFormatException("Pattern size too large for board!");
                }
                else*/
                if (gameBoard instanceof DynamicBoard) {
                    if (loadBoard.length > ((DynamicBoard) gameBoard).getMAXSIZE() || loadBoard[0].length > (
                            (DynamicBoard) gameBoard).getMAXSIZE()) {
                        throw new PatternFormatException("Pattern size " +
                                "too large for dynamic board size of " + ((DynamicBoard) gameBoard).getMAXSIZE() + " x" +
                                " " + ((DynamicBoard) gameBoard).getMAXSIZE());
                    }
                    ((DynamicBoard) gameBoard).expand(2 * (loadBoard.length - gameBoard.getWIDTH()), 2 * (loadBoard[0]
                            .length -
                            gameBoard.getWIDTH()));
                }
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
                animBtn.setText("Start");

                loadBoard = FileHandler.readFromURL(input);
                /*if (loadBoard.length > gameBoard.getWIDTH() || loadBoard[0].length > gameBoard.getHEIGHT()) {
                    throw new PatternFormatException("Pattern size too large for board!");
                }*/
                if (gameBoard instanceof DynamicBoard) {
                    if (loadBoard.length > ((DynamicBoard) gameBoard).getMAXSIZE() || loadBoard[0].length > (
                            (DynamicBoard) gameBoard).getMAXSIZE()) {
                        throw new PatternFormatException("Pattern size " +
                                "too large for dynamic board size of " + ((DynamicBoard) gameBoard).getMAXSIZE() + " x" +
                                " " + ((DynamicBoard) gameBoard).getMAXSIZE());
                    }
                    ((DynamicBoard) gameBoard).expand(2 * (loadBoard.length - gameBoard.getWIDTH()), 2 * (loadBoard[0]
                            .length -
                            gameBoard.getWIDTH()));
                }
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

    private void setPattern() {

        if (loadBoard.length > ((DynamicBoard) gameBoard).getMAXSIZE() || loadBoard[0].length > (
                (DynamicBoard) gameBoard).getMAXSIZE()) {
        }
        ((DynamicBoard) gameBoard).expand(2 * (loadBoard.length - gameBoard.getWIDTH()), 2 * (loadBoard[0]
                .length -
                gameBoard.getWIDTH()));

        int xOffset = (gameBoard.getWIDTH() - loadBoard.length) / 2;
        int yOffset = (gameBoard.getHEIGHT() - loadBoard[0].length) / 2;


        for (int i = 0; i < loadBoard.length; i++) {
            for (int j = 0; j < loadBoard[0].length; j++) {
                gameBoard.setCellState(i + xOffset, j + yOffset, loadBoard[i][j] == 1);
            }
        }

        gameBoard.setCellSize(Math.floor(cellSizeSlider.getValue()));
        readMeta();
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

    private void loadPattern(String resource) {
        try {
            loadBoard = FileHandler.readFromDisk(new File(resource));
            setPattern();
            draw();
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

//    @FXML
//    private void randomize(KeyEvent e) {
//        // Easter egg
//        if (e.getCode().toString().toLowerCase().equals("r")) {
//            backColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
//            cellColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
//            draw();
//        } else if (e.getCode().toString().toLowerCase().equals("d")) {
//            backColorPicker.setValue(Color.WHITE);
//            cellColorPicker.setValue(Color.BLACK);
//            draw();
//        }
//    }

    @FXML
    public void newTest() {
        gameBoard = new DynamicBoard();
        gameBoard.setCellSize(cellSizeSlider.getValue());
        draw();
        //System.out.println(dynamicBoard.toStringBoard());
    }
}
package Controller;

import Model.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

public class GameController implements Initializable {

    /**
     * Start/stop utton for the animation.
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

    private int xOffset, yOffset;

    @FXML
    private Button moveBtn;

    private boolean movingCells = false;

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

    /**
     * Draws the next generation of the pattern.
     */
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
     * give the correct mouse drag parameters to method {@code changeCellState}, depending on if the user clicked or
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
                    gc.fillRect((i * cS) + .25 + xOffset, (j * cS) + .25 + yOffset, cS - .5, cS - .5);
                }
            }
        }
    }

    @FXML
    public void toggleMovingCells() {
        // return !movingCells;
        if (movingCells)
            movingCells = false;
        else
            movingCells = true;
    }

    @FXML
    public void moveWithMouse(MouseEvent event) {
        if (movingCells) {
            int curX = 0;
            int curY = 0;

            for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                    if (gameBoard.getCellState(i, j)) {
                        curX = i + 1;
                        curY = j + 1;
                        break;
                    }
                }
            }

            int mouseX = (int) event.getX();
            int mouseY = (int) event.getY();

            xOffset = mouseX - curX;
            yOffset = mouseY - curY;

            if (mouseX < curX)
                xOffset = -xOffset;

            if (mouseY < curY)
                yOffset = -yOffset;
        }
    }

    /**
     * Handles all the key bindings associated with game control.
     * For moving the cells, a temporary {@code byte} array is used to store the current active cells, then draw them
     * one place further in the direction of the corresponding key pressed.
     *
     * @param event {@code KeyEvent} whose code is used to determine which key was pressed.
     */
    @FXML
    public void handleKeyEvents(KeyEvent event) {
        // temporary array must take one more value at each dimension than the gameBoard
        boolean[][] temp = new boolean[gameBoard.getWIDTH() + 1][gameBoard.getHEIGHT() + 1];

        switch (event.getCode().toString().toLowerCase()) {
            case "w":
                try {
                    // move cells one spot up and copy to temp
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (gameBoard.getCellState(i, j))
                                temp[i][j - 1] = true;
                        }
                    }

                    // clear cells from old spot
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            gameBoard.setCellState(i, j, false);
                        }
                    }

                    // copy values from temp array to gameBoard
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (temp[i][j])
                                gameBoard.setCellState(i, j, true);
                        }
                    }

                    // draw normally
                    draw();
                    break;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    // if exception is caught, place active cells in same positions in temp
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (gameBoard.getCellState(i, j))
                                temp[i][j] = true;
                        }
                    }
                    // copy the cells in the same places back to gameBoard
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (temp[i][j])
                                gameBoard.setCellState(i, j, true);
                        }
                    }
                    break;
                }

            case "d":
                // move cells one spot right and copy to temp
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (gameBoard.getCellState(i, j))
                            temp[i + 1][j] = true;
                    }
                }

                // clear cells from old spot
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        gameBoard.setCellState(i, j, false);
                    }
                }

                // copy cells back to gameBoard
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (temp[i][j])
                            gameBoard.setCellState(i, j, true);
                    }
                }

                // draw normally
                draw();
                break;

            case "s":
                // move cells one spot down and copy to temp
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (gameBoard.getCellState(i, j))
                            temp[i][j + 1] = true;
                    }
                }

                // clear cells from old spot
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        gameBoard.setCellState(i, j, false);
                    }
                }

                // copy cells back to gameBoard
                for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                    for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                        if (temp[i][j])
                            gameBoard.setCellState(i, j, true);
                    }
                }

                // draw normally
                draw();
                break;
            case "a":
                try {
                    // move cells one spot left and copy to temp
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (gameBoard.getCellState(i, j))
                                temp[i - 1][j] = true;
                        }
                    }

                    // clear cells from old spot
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            gameBoard.setCellState(i, j, false);
                        }
                    }

                    // copy cells back to gameBoard
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (temp[i][j])
                                gameBoard.setCellState(i, j, true);
                        }
                    }

                    // draw cells on updated positions
                    draw();
                    break;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    // exception handled in same way as for 'w'-case
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (gameBoard.getCellState(i, j))
                                temp[i][j] = true;
                        }
                    }
                    for (int i = 0; i < gameBoard.getWIDTH(); i++) {
                        for (int j = 0; j < gameBoard.getHEIGHT(); j++) {
                            if (temp[i][j])
                                gameBoard.setCellState(i, j, true);
                        }
                    }
                    break;
                }
            case "r":
                // Shh! Don't spoil this!
                if (event.isShiftDown()) {
                    backColorPicker.setValue(Color.WHITE);
                    cellColorPicker.setValue(Color.BLACK);
                    draw();
                    break;
                } else {
                    backColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
                    cellColorPicker.setValue(new Color(Math.random(), Math.random(), Math.random(), 1));
                    draw();
                    break;
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

    /**
     * Draws the grid that covers the {@code gameBoard}. The space between the lines of the grid is specified by the
     * current cell size.
     */
    private void drawGrid() {
        // get dimensions of gameBoard and cell size
        int width = gameBoard.getWIDTH();
        int height = gameBoard.getHEIGHT();
        double cS = cellSizeSlider.getValue();

        // set the grid color equal to the cell color and adjust line width
        gc.setStroke(cellColorPicker.getValue());
        gc.setLineWidth(0.1);

        // draw grid lines along the width and height of gameBoard
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
        TIMELINE.stop();
        animBtn.setText("Start");

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

    /**
     * Method {@code loadFileDisk} is used to load pattern files from the user's computer. It uses a
     * {@code FileChooser} to provide access to internally stored files. The supported file types are .cells and .rle.
     * If a file cannot be opened, either a {@code PatternFormatException} or {@code IOException} is thrown.
     *
     * @see FileHandler#readFromDisk(File)
     * @see PatternFormatException
     */
    @FXML
    private void loadFileDisk() {
        // prepare stage for opening file
        TIMELINE.stop();
        animBtn.setText("Start");
        // instantiate Alert object in case of error
        Alert alert = new Alert(Alert.AlertType.ERROR);

        try {
            // instantiate FileChooser and specify extensions
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Cell patterns", "*.cells", "*.rle"));
            // get the file that was chosen
            File selectedFile = fileChooser.showOpenDialog(null);


            if (selectedFile != null) {
                // call FileHandler method readFromDisk and assign returned value to loadBoard
                loadBoard = FileHandler.readFromDisk(selectedFile);

                if (gameBoard instanceof DynamicBoard) {
                    // throw PatternFormatException if loadBoard is too large
                    if (loadBoard.length > ((DynamicBoard) gameBoard).getMAXSIZE() || loadBoard[0].length > (
                            (DynamicBoard) gameBoard).getMAXSIZE()) {
                        throw new PatternFormatException("Pattern size " +
                                "too large for dynamic board size of " + ((DynamicBoard) gameBoard).getMAXSIZE() + " x" +
                                " " + ((DynamicBoard) gameBoard).getMAXSIZE());
                    }
                    // expand gameBoard to fit loadBoard
                    ((DynamicBoard) gameBoard).expand(2 * (loadBoard.length - gameBoard.getWIDTH()), 2 * (loadBoard[0]
                            .length - gameBoard.getWIDTH()));
                }
                // implement loaded pattern
                setPattern();
            }
            // make use of alert window in case of exception
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

        // draw normally
        draw();
    }

    /**
     * Method {@code #loadFileNet()} is used to open a connection to a website containing a pattern file and implement
     * the pattern in the game. The user is prompted for a URL, which is used as the {@code String} value passed to
     * {@code FileHandler}. The supported file types are .cells and .rle. If errors occur, the method throws
     * a {@code PatternFormatException} or an {@code IOException}.
     *
     * @see FileHandler#readFromURL(String)
     * @see PatternFormatException
     */
    @FXML
    private void loadFileNet() {
        // prepare text input dialog
        textInputDialog.setTitle("Load file from URL");
        textInputDialog.setHeaderText("Enter URL to GoL file");
        textInputDialog.showAndWait();

        // instantiate alert window in case of errors
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // get input String and clear text input dialog
        String input = textInputDialog.getResult();
        textInputDialog.getEditor().setText("");

        if (!(input == null))
            try {
                // stop animation
                TIMELINE.stop();
                animBtn.setText("Start");

                // parse input String and assign returned value to loadBoard
                loadBoard = FileHandler.readFromURL(input);

                if (gameBoard instanceof DynamicBoard) {
                    // throw PatternFormatException if loadBoard dimensions are greater than gameBoard dimensions
                    if (loadBoard.length > ((DynamicBoard) gameBoard).getMAXSIZE() || loadBoard[0].length > (
                            (DynamicBoard) gameBoard).getMAXSIZE()) {
                        throw new PatternFormatException("Pattern size " +
                                "too large for dynamic board size of " + ((DynamicBoard) gameBoard).getMAXSIZE() + " x" +
                                " " + ((DynamicBoard) gameBoard).getMAXSIZE());
                    }
                    // expand gameBoard to appropriate dimensions
                    ((DynamicBoard) gameBoard).expand(2 * (loadBoard.length - gameBoard.getWIDTH()), 2 * (loadBoard[0]
                            .length - gameBoard.getWIDTH()));
                }
                // implement loaded pattern
                setPattern();

            } catch (PatternFormatException pfe) {
                // inform user in case of pattern error
                alert.setHeaderText("Pattern error!");
                alert.setContentText(pfe.getMessage());
                alert.showAndWait();

            } catch (IOException ioe) {
                // ask for new URL in case of malformed input
                alert.setHeaderText("Something went wrong");
                alert.setContentText("Please try again with a correct URL!");
                alert.showAndWait();
            }
        // draw loaded pattern
        draw();
    }

    /**
     * This method is used in {@code loadFileDisk()} and {@code loadFileNet()} to implement the loaded patterns in the
     * {@code gameBoard}.
     */
    private void setPattern() {
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

    /**
     * {@code readMeta()} is used for displaying metadata about files that get loaded into the game. Method
     * {@code getMeta()} in the {@code FileHandler} class extracts the metadata from a file. The data is then
     * printed to its dedicated labels in the game.
     *
     * @see FileHandler#getMeta()
     * @see FileHandler#handleMeta()
     */
    private void readMeta() {
        // get metadata from file
        ArrayList<String> meta = FileHandler.getMeta();

        // print shape and author info if it exists
        shapeLabel.setText(meta.get(0) != null ? meta.get(0) : "No info...");
        authorLabel.setText(meta.get(1) != null ? meta.get(1) : "No info...");
    }

    /**
     * Clears the metadata labels and sets their text to default.
     * Used in {@code clearBoard()} method.
     */
    private void clearMetaLabels() {
        String s = "No info...";
        shapeLabel.setText(s);
        authorLabel.setText(s);
    }

    /**
     * This method is used for loading the provided default patterns under the 'Shape' menu. The files are loaded in
     * the same way as any disk file, only these files are found by default in the 'resources' package.
     * A {@code PatternFormatException} or an {@code IOException} can be thrown, but tests have shown that the chances
     * of this occurring are minimal.
     *
     * @param resource File path and name of the desired pattern
     * @see #loadFileDisk()
     * @see FileHandler#readFromDisk(File)
     * @see PatternFormatException
     */
    private void loadPattern(String resource) {
        try {
            // parse file and assign returned value to loadBoard
            loadBoard = FileHandler.readFromDisk(new File(resource));

            // implement pattern and draw
            setPattern();
            draw();
            // print exception messages to console
        } catch (IOException e) {
            System.out.println("Something went wrong...");
            System.out.println(e.getMessage());
        } catch (PatternFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Shows the help view in a separate stage when triggered. The {@code HelpView.fxml} and {@code HelpController}
     * files are loaded. Method {@code setUpStage} in the controller prepares the help view.
     *
     * @see HelpController
     * @see HelpController#setUpStage(Stage)
     */
    @FXML
    public void showHelp() {
        try {
            // initiate new Stage
            Stage stage = new Stage();
            stage.initOwner(playArea.getScene().getWindow());

            // instantiate and load help view controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HelpView.fxml"));
            Parent root = loader.load();
            HelpController helpController = loader.getController();
            helpController.setUpStage(stage);

            // show help
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ioe) {
            // print exception messages to console
            System.out.println("Something went wrong...");
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Opens the Life Wiki homepage in the default browser.
     */
    @FXML
    public void lifeWiki() {
        try {
            URI uri = new URI("http://www.conwaylife.com/wiki/Main_Page");
            java.awt.Desktop.getDesktop().browse(uri);
        }
        // print IOException message to console
        catch (IOException ex1) {
            System.out.println("Something went wrong...");
            System.out.println(ex1.getMessage());
        } catch (URISyntaxException ex2) {
            // this exception cannot occur because the uri is not specified by the user
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
//    public void newTest() {
//        gameBoard = new DynamicBoard();
//        gameBoard.setCellSize(cellSizeSlider.getValue());
//        draw();
//        //System.out.println(dynamicBoard.toStringBoard());
//    }
}
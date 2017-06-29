package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller class for the help view. Implements help view design features and handles button functionality.
 *
 * @author Branislav Petrović
 */
public class HelpController {
    @FXML
    public Label title;

    @FXML
    public Label subtitle1;

    @FXML
    public Label subtitle2;

    @FXML
    public Label subtitle3;

    @FXML
    public Button closeBtn;

    /**
     * Defines styling and layout for the help view {@code Stage}.
     *
     * @param stage
     *      Stage to apply effects on.
     * */
    void setUpStage(Stage stage) {
        stage.setResizable(false);

        title.setStyle("-fx-font: 24 Calibri;");

        subtitle1.setStyle("-fx-font: 20 Calibri;");

        subtitle2.setStyle("-fx-font: 20 Calibri;");

        subtitle3.setStyle("-fx-font: 20 Calibri;");
    }

    /**
     * Closes help view.
     * */
    @FXML
    public void close() {
        Stage stageToClose = (Stage) closeBtn.getScene().getWindow();
        stageToClose.close();
    }
}

package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Created by Bane on 02.05.2017.
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

    public void setUpStage(Stage stage) {
        stage.setResizable(false);

        title.setStyle("-fx-font: 24 Calibri;");
        // title.setUnderline(true);

        subtitle1.setStyle("-fx-font: 20 Calibri;");

        subtitle2.setStyle("-fx-font: 20 Calibri;");

        subtitle3.setStyle("-fx-font: 20 Calibri;");
    }

    @FXML
    public void close() {
        Stage stageToClose = (Stage) closeBtn.getScene().getWindow();
        stageToClose.close();
    }
}

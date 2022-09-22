package pb.databasegamemaker;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginStage implements Initializable {
    @FXML private TextField db;
    @FXML private TextField un;
    @FXML private PasswordField pw;
    @FXML private Label result;

    @FXML
    public void login() {
        Map<String, String> params = new HashMap<>();
        params.put("javax.persistence.jdbc.url", "jdbc:postgresql://" + db.getText());
        params.put("javax.persistence.jdbc.user", un.getText());
        params.put("javax.persistence.jdbc.password", pw.getText());
        EntityManagerFactory emf = null;

        try {
            emf = Persistence.createEntityManagerFactory("DBGM-unit", params);
            DatabaseGameMaker.emf = emf;
        }catch (Exception e){
            result.setText("Login Failed! - " + e.getMessage());
        }

        if(emf != null){
            pw.setText("");
            DatabaseGameMaker.instance.setScene("loader");
        }
    }

    @FXML
    public void exit(){
        DatabaseGameMaker.instance.stop();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result.setText("");
    }
}

package pb.databasegamemaker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayStage implements Initializable {
    @FXML private TableView<String> table;
    @FXML private TableColumn<String, String> msgCol;

    @FXML private TextField actField;

    private final ObservableList<String> data = FXCollections.observableArrayList();

    @FXML
    public void start(){
        data.clear();
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        em.createNativeQuery("select pb.start()").getResultList().forEach(o -> data.add((String)o));
        em.close();
    }

    @FXML
    public void act(){
        data.clear();
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        em.createNativeQuery("select pb.act(:action)").setParameter("action", actField.getText()).getResultList().forEach(o -> data.add((String)o));
        em.close();
    }

    @FXML
    public void logout(){
        DatabaseGameMaker.emf.close();
        DatabaseGameMaker.emf = null;
        DatabaseGameMaker.instance.setScene("login");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setItems(data);
        msgCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        int i = ((BigInteger)em.createNativeQuery("select count(*) from pb.active_users").getSingleResult()).intValue();
        em.close();

        if(i == 0) start();
    }

    @FXML
    private void changeRooms(){
        DatabaseGameMaker.instance.setScene("rooms");
    }

    @FXML
    private void changeActions(){
        DatabaseGameMaker.instance.setScene("actions");
    }

    @FXML
    private void changeItems(){
        DatabaseGameMaker.instance.setScene("items");
    }

    @FXML
    private void changeLocations(){
        DatabaseGameMaker.instance.setScene("locations");
    }

    @FXML
    private void changePlay(){
    }
}

package pb.databasegamemaker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.net.URL;
import java.util.ResourceBundle;

public class ActionStage implements Initializable {
    @FXML private TableView<ActionData> table;
    @FXML private TableColumn<ActionData, String> actionCol;
    @FXML private Label result;
    @FXML private TextField actionField;

    private final ObservableList<ActionData> data = FXCollections.observableArrayList();
    @FXML
    public void add(){
        data.add(new ActionData(actionField.getText()));
    }

    @FXML
    public void logout(){
        DatabaseGameMaker.emf.close();
        DatabaseGameMaker.emf = null;
        DatabaseGameMaker.instance.setScene("login");
    }

    @FXML
    public void editAction(TableColumn.CellEditEvent<ActionData, String> event){
        ActionData action = event.getTableView().getItems().get(event.getTablePosition().getRow());
        action.setAction(event.getNewValue());
    }

    @FXML
    public void getData(){
        data.clear();
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        Query q = em.createQuery("from ActionEntity order by id");
        q.getResultList().forEach(o -> data.add(new ActionData((ActionEntity) o)));
        em.close();
    }

    @FXML
    public void pushData(){
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        em.getTransaction().begin();

        for(ActionData d : data){
            if(d.getAction().equals("")){
                Object o = em.find(ActionEntity.class, em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(d.getEntity()));
                if(o != null)
                    em.remove(o);
            }else{
                em.merge(d.getEntity());
            }
        }

        data.clear();
        Query q = em.createQuery("from ActionEntity order by id");
        q.getResultList().forEach(o -> data.add(new ActionData((ActionEntity) o)));

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result.setText("");
        actionField.setText("call pb.say('Hello');");
        table.setItems(data);

        getData();

        actionCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @FXML
    private void changeRooms(){
        DatabaseGameMaker.instance.setScene("rooms");
    }

    @FXML
    private void changeActions(){
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
        DatabaseGameMaker.instance.setScene("play");
    }
}

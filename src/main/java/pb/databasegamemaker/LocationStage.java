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

public class LocationStage implements Initializable {
    @FXML private TableView<LocationData> table;
    @FXML private TableColumn<LocationData, String> itemCol;
    @FXML private TableColumn<LocationData, String> roomCol;
    @FXML private TableColumn<LocationData, String> sideCol;
    @FXML private TextField itemField;
    @FXML private TextField roomField;
    @FXML private TextField sideField;
    @FXML private Label result;
    private final ObservableList<LocationData> data = FXCollections.observableArrayList();

    @FXML
    private void editItem(TableColumn.CellEditEvent<LocationData, String> event){
        LocationData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        try{
            d.setItem(event.getNewValue());
        }catch (Exception e){
            result.setText(e.getMessage());
        }
    }

    @FXML
    private void editRoom(TableColumn.CellEditEvent<LocationData, String> event){
        LocationData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        try{
            d.setRoom(event.getNewValue());
        }catch (Exception e){
            result.setText(e.getMessage());
        }
    }

    @FXML
    private void editSide(TableColumn.CellEditEvent<LocationData, String> event){
        LocationData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        try{
            d.setSide(event.getNewValue());
        }catch (Exception e){
            result.setText(e.getMessage());
        }
    }

    @FXML
    public void add(){
        LocationData d;
        try {
            d = new LocationData(itemField.getText(), roomField.getText(), sideField.getText());
        }catch (Exception e){
            result.setText(e.getMessage());
            return;
        }
        data.add(d);
    }

    @FXML
    public void logout(){
        DatabaseGameMaker.emf.close();
        DatabaseGameMaker.emf = null;
        DatabaseGameMaker.instance.setScene("login");
    }

    @FXML
    public void getData(){
        data.clear();
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        Query q = em.createQuery("from LocationEntity order by id");
        q.getResultList().forEach(o -> data.add(new LocationData((LocationEntity) o)));
        em.close();
    }

    @FXML
    public void pushData(){
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        em.getTransaction().begin();

        for(LocationData d : data){
            if(d.getItem().equals("") || d.getRoom().equals("")){
                Object o = em.find(LocationEntity.class, em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(d.getEntity()));
                if(o != null)
                    em.remove(o);
            }else{
                em.merge(d.getEntity());
            }
        }

        data.clear();
        Query q = em.createQuery("from LocationEntity order by id");
        q.getResultList().forEach(o -> data.add(new LocationData((LocationEntity) o)));

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result.setText("");
        itemField.setText("0");
        roomField.setText("0");
        sideField.setText("1");

        table.setItems(data);

        getData();

        itemCol.setCellFactory(TextFieldTableCell.forTableColumn());
        roomCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sideCol.setCellFactory(TextFieldTableCell.forTableColumn());
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
    }

    @FXML
    private void changePlay(){
        DatabaseGameMaker.instance.setScene("play");
    }
}

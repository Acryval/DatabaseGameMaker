package pb.databasegamemaker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.net.URL;
import java.util.ResourceBundle;

public class ItemStage implements Initializable {
    @FXML private TableView<ItemData> table;
    @FXML private TableColumn<ItemData, String> nameCol;
    @FXML private TableColumn<ItemData, String> useActionCol;
    @FXML private TableColumn<ItemData, String> goActionCol;
    @FXML private TableColumn<ItemData, String> grabableCol;
    @FXML private TableColumn<ItemData, String> targetCol;
    @FXML private Label result;
    @FXML private TextField nameField;
    @FXML private TextField useActionField;
    @FXML private TextField goActionField;
    @FXML private CheckBox grabableField;
    @FXML private TextField targetField;
    private final ObservableList<ItemData> data = FXCollections.observableArrayList();

    @FXML
    public void editName(TableColumn.CellEditEvent<ItemData, String> event){
        ItemData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        d.setName(event.getNewValue());
    }
    @FXML
    public void editUse(TableColumn.CellEditEvent<ItemData, String> event){
        ItemData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        try {
            d.setUseAction(event.getNewValue());
        }catch (Exception e){
            result.setText(e.getMessage());
        }
    }
    @FXML
    public void editGo(TableColumn.CellEditEvent<ItemData, String> event){
        ItemData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        try {
            d.setGoAction(event.getNewValue());
        }catch (Exception e){
            result.setText(e.getMessage());
        }
    }
    @FXML
    public void editGrab(TableColumn.CellEditEvent<ItemData, String> event){
        ItemData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        d.setGrabable(event.getNewValue());
    }
    @FXML
    public void editTarget(TableColumn.CellEditEvent<ItemData, String> event){
        ItemData d = event.getTableView().getItems().get(event.getTablePosition().getRow());
        try {
            d.setTarget(event.getNewValue());
        }catch (Exception e){
            result.setText(e.getMessage());
        }
    }

    @FXML
    public void add(){
        ItemData d;
        try {
            d = new ItemData(nameField.getText(), useActionField.getText(), goActionField.getText(), grabableField.isSelected(), targetField.getText());
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
        Query q = em.createQuery("from ItemEntity order by id");
        q.getResultList().forEach(o -> data.add(new ItemData((ItemEntity) o)));
        em.close();
    }

    @FXML
    public void pushData(){
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        em.getTransaction().begin();

        for(ItemData d : data){
            if(d.getName().equals("")){
                Object o = em.find(ItemEntity.class, em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(d.getEntity()));
                if(o != null)
                    em.remove(o);
            }else{
                em.merge(d.getEntity());
            }
        }

        data.clear();
        Query q = em.createQuery("from ItemEntity order by id");
        q.getResultList().forEach(o -> data.add(new ItemData((ItemEntity) o)));

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result.setText("");
        nameField.setText("name");
        useActionField.setText("0");
        goActionField.setText("0");
        grabableField.setSelected(true);
        targetField.setText("0");

        table.setItems(data);

        getData();

        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        useActionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        goActionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        grabableCol.setCellFactory(TextFieldTableCell.forTableColumn());
        targetCol.setCellFactory(TextFieldTableCell.forTableColumn());
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

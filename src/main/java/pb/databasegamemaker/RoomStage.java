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

public class RoomStage implements Initializable {

    @FXML private TableView<RoomData> table;
    @FXML private TableColumn<RoomData, String> nameCol;
    @FXML private TableColumn<RoomData, String> sidesCol;
    @FXML private Label result;

    @FXML private TextField nameField;
    @FXML private TextField sidesField;
    private final ObservableList<RoomData> data = FXCollections.observableArrayList();

    @FXML
    public void add(){
        data.add(new RoomData(nameField.getText(), sidesField.getText()));
    }

    @FXML
    public void logout(){
        DatabaseGameMaker.emf.close();
        DatabaseGameMaker.emf = null;
        DatabaseGameMaker.instance.setScene("login");
    }

    @FXML
    public void editName(TableColumn.CellEditEvent<RoomData, String> event){
        RoomData room = event.getTableView().getItems().get(event.getTablePosition().getRow());
        room.setName(event.getNewValue());
    }

    @FXML
    public void editSides(TableColumn.CellEditEvent<RoomData, String> event){
        RoomData room = event.getTableView().getItems().get(event.getTablePosition().getRow());
        if(event.getNewValue().startsWith("0")){
            room.setSides("1");
        }else {
            room.setSides(event.getNewValue());
        }
    }

    @FXML
    public void getData(){
        data.clear();
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        Query q = em.createQuery("from RoomEntity order by id");
        q.getResultList().forEach(o -> data.add(new RoomData((RoomEntity)o)));
        em.close();
    }

    @FXML
    public void pushData(){
        EntityManager em = DatabaseGameMaker.emf.createEntityManager();
        em.getTransaction().begin();

        em.createNativeQuery("truncate pb.active_users restart identity cascade;").executeUpdate();

        int sl_is = (int) em.createNativeQuery("select id from pb.start_location").getSingleResult();

        for(RoomData d : data){
            if(d.getName().equals("")){
                int delRoom_id = (int) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(d.getEntity());
                if(delRoom_id == sl_is){
                    em.createNativeQuery("truncate table pb.start_location").executeUpdate();
                }
                Object o = em.find(RoomEntity.class, delRoom_id);
                if(o != null)
                    em.remove(o);
            }else{
                em.merge(d.getEntity());
            }
        }

        data.clear();
        Query q = em.createQuery("from RoomEntity order by id");
        q.getResultList().forEach(o -> data.add(new RoomData((RoomEntity)o)));

        if(data.isEmpty()){
            RoomEntity r = new RoomEntity();
            r.setName("the first room");
            r.setSides(4);
            em.persist(r);

            em.createNativeQuery("insert into pb.start_location(id) values((select id from pb.data_rooms limit 1));").executeUpdate();

            data.add(new RoomData(r));
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result.setText("");
        nameField.setText("name");
        sidesField.setText("0");
        table.setItems(data);

        getData();

        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sidesCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @FXML
    private void changeRooms(){
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
        DatabaseGameMaker.instance.setScene("play");
    }
}

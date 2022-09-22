package pb.databasegamemaker;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ActionData {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty action;
    private final ActionEntity entity;

    public ActionData(String action){
        id = new SimpleIntegerProperty();
        this.action = new SimpleStringProperty(action);
        entity = new ActionEntity(this);
    }

    public ActionData(ActionEntity entity){
        id = new SimpleIntegerProperty(entity.getId());
        action = new SimpleStringProperty(entity.getAction());
        this.entity = entity;
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getAction() {
        return action.get();
    }

    public SimpleStringProperty actionProperty() {
        return action;
    }

    public void setAction(String action) {
        this.action.set(action);
        entity.setAction(action);
    }

    public ActionEntity getEntity() {
        return entity;
    }
}

package pb.databasegamemaker;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RoomData {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty sides;
    private final RoomEntity entity;

    public RoomData(String name, String sides){
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty(name);
        this.sides = new SimpleStringProperty(sides);
        entity = new RoomEntity(this);
    }

    public RoomData(RoomEntity entity) {
        id = new SimpleIntegerProperty(entity.getId());
        name = new SimpleStringProperty(entity.getName());
        sides = new SimpleStringProperty("" + entity.getSides());
        this.entity = entity;
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
        entity.setName(name);
    }

    public String getSides() {
        return sides.get();
    }

    public SimpleStringProperty sidesProperty() {
        return sides;
    }

    public void setSides(String sides) {
        this.sides.set(sides);
        try {
            entity.setSides(Integer.parseInt(sides));
        }catch (NumberFormatException ignored){
            this.sides.set("1");
            entity.setSides(1);
        }
    }

    public RoomEntity getEntity() {
        return entity;
    }
}

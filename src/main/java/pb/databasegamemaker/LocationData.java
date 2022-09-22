package pb.databasegamemaker;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.persistence.EntityManager;

public class LocationData {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty item;
    private final SimpleStringProperty room;
    private final SimpleStringProperty side;
    private final LocationEntity entity;

    public LocationData(String item_id, String room_id, String side) throws Exception{
        id = new SimpleIntegerProperty();
        item = new SimpleStringProperty(item_id);
        room = new SimpleStringProperty(room_id);
        this.side = new SimpleStringProperty(side);
        entity = new LocationEntity(this);
    }

    public LocationData(LocationEntity entity){
        id = new SimpleIntegerProperty(entity.getId());
        item = new SimpleStringProperty(entity.getItem() != null ? String.valueOf(entity.getItem().getId()) : "");
        room = new SimpleStringProperty(entity.getRoom() != null ? String.valueOf(entity.getRoom().getId()) : "");
        side = new SimpleStringProperty("" + entity.getSide());
        this.entity = entity;
    }

    public int getId() {
        return id.get();
    }

    public String getItem() {
        return item.get();
    }

    public void setItem(String item) throws Exception {
        this.item.set(item);

        if(item.equals("")){
            entity.setItem(null);
        }else{
            EntityManager em = DatabaseGameMaker.emf.createEntityManager();
            try{
                entity.setItem(em.find(ItemEntity.class, Integer.parseInt(item)));
                if(entity.getItem() == null) throw new Exception("Item with id: " + item + " does not exist");
            }catch (Exception ignored){
                entity.setItem(null);
                throw new Exception("Item with id: " + item + " does not exist");
            }finally {
                em.close();
            }
        }
    }

    public String getRoom() {
        return room.get();
    }

    public void setRoom(String room) throws Exception{
        this.room.set(room);

        setSide("1");

        if(room.equals("")){
            entity.setRoom(null);
        }else{
            EntityManager em = DatabaseGameMaker.emf.createEntityManager();
            try{
                entity.setRoom(em.find(RoomEntity.class, Integer.parseInt(room)));
                if(entity.getRoom() == null) throw new Exception("Room with id: " + room + " does not exist");
            }catch (Exception ignored){
                entity.setRoom(null);
                throw new Exception("Room with id: " + room + " does not exist");
            }finally {
                em.close();
            }
        }
    }

    public String getSide() {
        return side.get();
    }

    public void setSide(String side) throws Exception {
        this.side.set(side);
        if(entity.getRoom() == null){
            this.side.set("1");
            throw new Exception("Room is empty");
        }

        try{
            int s = Integer.parseInt(side);
            if(s <= 0 || s > entity.getRoom().getSides()){
                entity.setSide(1);
                this.side.set("1");
            }else{
                entity.setSide(s);
            }
        }catch (NumberFormatException ignored){
            entity.setSide(1);
            this.side.set("1");
        }
    }

    public LocationEntity getEntity() {
        return entity;
    }
}
